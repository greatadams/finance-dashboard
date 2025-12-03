package com.pm.greatadamu.transactionservice.service;

import com.pm.greatadamu.grpc.account.UpdateBalanceResponse;
import com.pm.greatadamu.grpc.account.ValidateAccountResponse;
import com.pm.greatadamu.transactionservice.dto.TransactionRequestDTO;
import com.pm.greatadamu.transactionservice.dto.TransactionResponseDTO;
import com.pm.greatadamu.transactionservice.gRPC.AccountGrpcClient;
import com.pm.greatadamu.transactionservice.kafka.TransactionEvent;
import com.pm.greatadamu.transactionservice.kafka.TransactionEventProducer;
import com.pm.greatadamu.transactionservice.mapper.TransactionMapper;
import com.pm.greatadamu.transactionservice.model.Transaction;
import com.pm.greatadamu.transactionservice.model.TransactionStatus;
import com.pm.greatadamu.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServices {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionEventProducer transactionEventProducer;
    private final AccountGrpcClient accountGrpcClient;


    //Return Transaction record from dB
    public List<TransactionResponseDTO> getTransactions() {
        //get all transaction from DB
        List<Transaction> transactions = transactionRepository.findAll();

        //map transaction to transactionResponseDTO using transactionMapper
        //CONVERT EACH TRANSACTION->TransactionResponseDTO
        return transactions.stream()
                .map(transactionMapper::mapToResponseDTO)
                .toList();
    }

    /**
     * Create a Transaction with gRPC validation and balance updates
     */
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        log.info("Creating transaction: {} from account {} to account {}",
                dto.getAmount(),
                dto.getFromAccountId(),
                dto.getToAccountId());

        // ========== STEP 1: Validate Source Account ==========
        ValidateAccountResponse sourceValidation =
                accountGrpcClient.validateAccount(dto.getFromAccountId());

        if (!sourceValidation.getExists()) {
            throw new RuntimeException("Source account not found");
        }
        if (!sourceValidation.getIsActive()) {
            throw new RuntimeException("Source account is not active");
        }

        // ========== STEP 2: Validate Destination Account ==========
        ValidateAccountResponse destValidation =
                accountGrpcClient.validateAccount(dto.getToAccountId());

        if (!destValidation.getExists()) {
            throw new RuntimeException("Destination account not found");
        }
        if (!destValidation.getIsActive()) {
            throw new RuntimeException("Destination account is not active");
        }

        // ========== STEP 3: Create Transaction with PENDING Status ==========
        Transaction transaction = transactionMapper.mapToEntity(dto);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setTransactionDate(LocalDateTime.now());

        // Save transaction as PENDING
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {} and status: PENDING", savedTransaction.getId());

        try {
            // ========== STEP 4: Debit Source Account via gRPC ==========
            String description = "Transaction #" + savedTransaction.getId();
            UpdateBalanceResponse debitResponse = accountGrpcClient.debitAccount(
                    dto.getFromAccountId(),
                    dto.getAmount(),
                    description
            );

            if (!debitResponse.getSuccess()) {
                throw new RuntimeException("Failed to debit source account: " + debitResponse.getMessage());
            }

            log.info("Source account {} debited. New balance: {}",
                    dto.getFromAccountId(), debitResponse.getNewBalance());

            // ========== STEP 5: Credit Destination Account via gRPC ==========
            UpdateBalanceResponse creditResponse = accountGrpcClient.creditAccount(
                    dto.getToAccountId(),
                    dto.getAmount(),
                    description
            );

            if (!creditResponse.getSuccess()) {
                // ROLLBACK: Credit back to source account
                log.error("Failed to credit destination account, rolling back...");
                accountGrpcClient.creditAccount(
                        dto.getFromAccountId(),
                        dto.getAmount(),
                        "Rollback: " + description
                );
                throw new RuntimeException("Failed to credit destination account: " + creditResponse.getMessage());
            }

            log.info("Destination account {} credited. New balance: {}",
                    dto.getToAccountId(), creditResponse.getNewBalance());

            // ========== STEP 6: Update Transaction to COMPLETED ==========
            savedTransaction.setTransactionStatus(TransactionStatus.COMPLETED);
            savedTransaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(savedTransaction);

            log.info("Transaction {} completed successfully", savedTransaction.getId());

            // ========== STEP 7: Publish to Kafka ==========
            TransactionEvent event = new TransactionEvent(
                    savedTransaction.getId(),
                    savedTransaction.getCustomerId(),
                    savedTransaction.getAmount(),
                    savedTransaction.getTransactionType(),
                    savedTransaction.getTransactionStatus(),
                    savedTransaction.getTransactionDate()
            );
            transactionEventProducer.sendTransactionEvent(event);
            log.info("TransactionEvent published for transaction ID: {}", savedTransaction.getId());

        } catch (Exception e) {
            // ========== ERROR: Update Transaction to FAILED ==========
            log.error("Transaction {} failed: {}", savedTransaction.getId(), e.getMessage());
            savedTransaction.setTransactionStatus(TransactionStatus.FAILED);
            savedTransaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(savedTransaction);

            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }

        // ========== STEP 8: Return Response ==========
        return transactionMapper.mapToResponseDTO(savedTransaction);
    }

    //Get a particular Transaction By Transaction id
    public TransactionResponseDTO getTransactionByTransactionId(Long transactionId) {
        //get transaction ID from dB
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(()->
                new RuntimeException("Transaction with id " + transactionId + " not found"));

        //convert to JSON->Transaction entity to TransactionResponseDto
        return transactionMapper.mapToResponseDTO(transaction);
    }

    //Get all Transaction done By customer via id
    public List<TransactionResponseDTO> getTransactionsByCustomerId(Long customerId) {
        //get customer ID from dB
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

        if (transactions.isEmpty()) {
            throw new RuntimeException("No Transactions found fot Customer with id " + customerId);
        }
        return transactions.stream()
                .map(transactionMapper::mapToResponseDTO)
                .toList();
    }

}

package com.pm.greatadamu.transactionservice.mapper;

import com.pm.greatadamu.transactionservice.dto.TransactionRequestDTO;
import com.pm.greatadamu.transactionservice.dto.TransactionResponseDTO;
import com.pm.greatadamu.transactionservice.model.Transaction;
import com.pm.greatadamu.transactionservice.model.TransactionStatus;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
public class TransactionMapper {
    public Transaction mapToEntity(TransactionRequestDTO transactionRequestDTO) {
            return Transaction.builder()
                    .customerId(transactionRequestDTO.getCustomerId())
                    .amount(transactionRequestDTO.getAmount())
                    .fromAccountId(transactionRequestDTO.getFromAccountId())
                    .toAccountId(transactionRequestDTO.getToAccountId())
                    .description(transactionRequestDTO.getDescription())
                    .transactionType(transactionRequestDTO.getTransactionType())
                    .transactionStatus(TransactionStatus.PENDING)//system sets this
                    .transactionDate(LocalDateTime.now())//system set this
                    .build();
    }

    public TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getAmount(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getDescription(),
                transaction.getTransactionType(),
                transaction.getTransactionStatus(),
                transaction.getTransactionDate()
        );
    }

}

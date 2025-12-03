package com.pm.greatadamu.accountservice.gRPC;

import com.pm.greatadamu.accountservice.model.Account;
import com.pm.greatadamu.accountservice.model.AccountStatus;
import com.pm.greatadamu.accountservice.repository.AccountRepository;
import com.pm.greatadamu.grpc.account.*;
import com.pm.greatadamu.grpc.account.AccountServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private final AccountRepository accountRepository;

    /**
     * Validate if account exists and is active
     */
    @Override
    public void validateAccount(ValidateAccountRequest request,
                                StreamObserver<ValidateAccountResponse> responseObserver) {
        long accountId = request.getAccountId();
        log.info("gRPC Server: Validating account ID: {}", accountId);

        try {
            Account account = accountRepository.findById(accountId).orElse(null);

            ValidateAccountResponse response;
            if (account == null) {
                response = ValidateAccountResponse.newBuilder()
                        .setExists(false)
                        .setIsActive(false)
                        .setMessage("Account not found")
                        .build();
                log.info("gRPC Server: Account {} not found", accountId);
            } else {
                boolean isActive = account.getAccountStatus() == AccountStatus.ACTIVE;
                response = ValidateAccountResponse.newBuilder()
                        .setExists(true)
                        .setIsActive(isActive)
                        .setMessage(isActive ? "Account is active" : "Account is not active")
                        .build();
                log.info("gRPC Server: Account {} exists, active: {}", accountId, isActive);
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC Server: Error validating account {}: {}", accountId, e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error validating account: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Get account balance
     */
    @Override
    public void getBalance(GetBalanceRequest request,
                           StreamObserver<GetBalanceResponse> responseObserver) {
        long accountId = request.getAccountId();
        log.info("gRPC Server: Getting balance for account ID: {}", accountId);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            GetBalanceResponse response = GetBalanceResponse.newBuilder()
                    .setBalance(account.getAccountBalance().toString())
                    .setCurrency("USD")
                    .build();

            log.info("gRPC Server: Account {} balance: {}", accountId, account.getAccountBalance());

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC Server: Error getting balance for account {}: {}", accountId, e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Update account balance (debit or credit)
     */
    @Override
    @Transactional
    public void updateBalance(UpdateBalanceRequest request,
                              StreamObserver<UpdateBalanceResponse> responseObserver) {
        long accountId = request.getAccountId();
        BigDecimal amount = new BigDecimal(request.getAmount());
        OperationType operation = request.getOperation();
        String description = request.getDescription();

        log.info("gRPC Server: {} {} for account ID: {}",
                operation == OperationType.DEBIT ? "Debiting" : "Crediting",
                amount, accountId);

        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            BigDecimal currentBalance = account.getAccountBalance();
            BigDecimal newBalance;

            if (operation == OperationType.DEBIT) {
                // Subtract from balance
                if (currentBalance.compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient balance. Current: " +
                            currentBalance + ", Required: " + amount);
                }
                newBalance = currentBalance.subtract(amount);
                log.info("gRPC Server: Debiting {} from account {}. Current: {}, New: {}",
                        amount, accountId, currentBalance, newBalance);
            } else {
                // Add to balance
                newBalance = currentBalance.add(amount);
                log.info("gRPC Server: Crediting {} to account {}. Current: {}, New: {}",
                        amount, accountId, currentBalance, newBalance);
            }

            // Update balance
            account.setAccountBalance(newBalance);
            accountRepository.save(account);

            UpdateBalanceResponse response = UpdateBalanceResponse.newBuilder()
                    .setSuccess(true)
                    .setNewBalance(newBalance.toString())
                    .setMessage("Balance updated successfully")
                    .build();

            log.info("gRPC Server: Balance update successful for account {}", accountId);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC Server: Error updating balance for account {}: {}", accountId, e.getMessage());

            UpdateBalanceResponse response = UpdateBalanceResponse.newBuilder()
                    .setSuccess(false)
                    .setNewBalance("0")
                    .setMessage("Error: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
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
        OperationType operation = request.getOperation();
        String description = request.getDescription();

        //parse amount safely + validate
        final BigDecimal amount;
        try {
            String rawAmount = request.getAmount();

            if (rawAmount == null || rawAmount.isBlank()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Amount required")
                        .asRuntimeException());
                return;
            }
            amount = new BigDecimal(rawAmount);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                //amount must be >0
                responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Amount must be greater than zero")
                        .asRuntimeException());
                return;
            }
        }catch (NumberFormatException e) {
            //handle bad numbers properly
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid amount format")
                    .asRuntimeException());
            return;

        }
        log.info("gRPC Server: {} {} for account ID: {}",
                operation == OperationType.DEBIT ? "Debiting" : "Crediting",
                amount, accountId);

        try {
            Account account = accountRepository.findByIdForUpdate(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            BigDecimal currentBalance = account.getAccountBalance();
            BigDecimal newBalance;

            if (operation == OperationType.DEBIT) {
                // Subtract from balance
                if (currentBalance.compareTo(amount) < 0) {
                    responseObserver.onError(Status.FAILED_PRECONDITION
                            .withDescription("Insufficient funds. Current: "  + currentBalance + ", Required: " + amount)
                    .asRuntimeException());
                    return;
                }
                newBalance = currentBalance.subtract(amount);
                log.info("gRPC Server: Debiting {} from account {}. Current: {}, New: {}",
                        amount, accountId, currentBalance, newBalance);
            } else if (operation == OperationType.CREDIT) {
                // Add to balance
                newBalance = currentBalance.add(amount);
                log.info("gRPC Server: Crediting {} to account {}. Current: {}, New: {}",
                        amount, accountId, currentBalance, newBalance);
            }else {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Unknown operation type")
                        .asRuntimeException());
                return;
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

        } catch (RuntimeException e) {
            if ("Acount not found".equals(e.getMessage())) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(e.getMessage())
                        .asRuntimeException());
                return;
            }
            log.error("gRPC Server: Error updating balance for account {}: {}", accountId, e.getMessage());

            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error updating balance: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
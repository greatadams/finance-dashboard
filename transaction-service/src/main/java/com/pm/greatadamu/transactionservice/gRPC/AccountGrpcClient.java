package com.pm.greatadamu.transactionservice.gRPC;

import com.pm.greatadamu.grpc.account.*;
import com.pm.greatadamu.grpc.account.AccountServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class AccountGrpcClient {

    @Value("${grpc.account-service.host:localhost}")
    private String accountServiceHost;

    @Value("${grpc.account-service.port:9090}")
    private int accountServicePort;

    private ManagedChannel channel;
    private AccountServiceGrpc.AccountServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        log.info("Initializing gRPC client for Account Service at {}:{}",
                accountServiceHost, accountServicePort);

        channel = ManagedChannelBuilder
                .forAddress(accountServiceHost, accountServicePort)
                .usePlaintext()
                .build();

        blockingStub = AccountServiceGrpc.newBlockingStub(channel);

        log.info("gRPC client initialized successfully");
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            log.info("Shutting down gRPC channel");
            channel.shutdown();
        }
    }

    /**
     * Validate if account exists and is active
     */
    public ValidateAccountResponse validateAccount(Long accountId) {
        log.info("gRPC Client: Validating account ID: {}", accountId);

        ValidateAccountRequest request = ValidateAccountRequest.newBuilder()
                .setAccountId(accountId)
                .build();

        ValidateAccountResponse response = blockingStub.validateAccount(request);

        log.info("gRPC Client: Validation result for account {}: exists={}, active={}",
                accountId, response.getExists(), response.getIsActive());

        return response;
    }
    /**
     * Get account balance
     */
    public GetBalanceResponse getBalance(Long accountId) {
        log.info("gRPC Client: Getting balance for account ID: {}", accountId);

        GetBalanceRequest request = GetBalanceRequest.newBuilder()
                .setAccountId(accountId)
                .build();

        GetBalanceResponse response = blockingStub.getBalance(request);

        log.info("gRPC Client: Balance for account {}: {}", accountId, response.getBalance());

        return response;
    }

    /**
     * Debit account (subtract from balance)
     */
    public UpdateBalanceResponse debitAccount(Long accountId, BigDecimal amount, String description) {
        log.info("gRPC Client: Debiting {} from account ID: {}", amount, accountId);

        UpdateBalanceRequest request = UpdateBalanceRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(amount.toString())
                .setOperation(OperationType.DEBIT)
                .setDescription(description)
                .build();

        UpdateBalanceResponse response = blockingStub.updateBalance(request);

        log.info("gRPC Client: Debit result for account {}: success={}, new balance={}",
                accountId, response.getSuccess(), response.getNewBalance());

        return response;
    }

    /**
     * Credit account (add to balance)
     */
    public UpdateBalanceResponse creditAccount(Long accountId, BigDecimal amount, String description) {
        log.info("gRPC Client: Crediting {} to account ID: {}", amount, accountId);

        UpdateBalanceRequest request = UpdateBalanceRequest.newBuilder()
                .setAccountId(accountId)
                .setAmount(amount.toString())
                .setOperation(OperationType.CREDIT)
                .setDescription(description)
                .build();

        UpdateBalanceResponse response = blockingStub.updateBalance(request);

        log.info("gRPC Client: Credit result for account {}: success={}, new balance={}",
                accountId, response.getSuccess(), response.getNewBalance());

        return response;
    }

}

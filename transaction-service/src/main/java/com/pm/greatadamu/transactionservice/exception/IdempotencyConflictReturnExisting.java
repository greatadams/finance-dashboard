package com.pm.greatadamu.transactionservice.exception;

import com.pm.greatadamu.transactionservice.dto.TransactionResponseDTO;
import lombok.Getter;

@Getter

public class IdempotencyConflictReturnExisting extends RuntimeException {
   private final TransactionResponseDTO existing;
    public  IdempotencyConflictReturnExisting(TransactionResponseDTO existing) {
        super("Transaction with this idempotency key already exists");
        this.existing = existing;
    }
}

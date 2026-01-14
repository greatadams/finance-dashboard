package com.pm.greatadamu.transactionservice.dto;

import com.pm.greatadamu.transactionservice.model.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    @NotNull
    private Long customerId;

    private String idempotencyKey;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long fromAccountId;

    @NotNull
    private Long toAccountId;

    @Size(max = 500)
    private String description;

    @NotNull
    private TransactionType transactionType;
}

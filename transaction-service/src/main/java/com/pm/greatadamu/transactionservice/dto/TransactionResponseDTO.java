package com.pm.greatadamu.transactionservice.dto;

import com.pm.greatadamu.transactionservice.model.TransactionStatus;
import com.pm.greatadamu.transactionservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private Long customerId;
    private BigDecimal amount;
    private Long fromAccountId;
    private Long toAccountId;
    private String description;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime transactionDate;

}

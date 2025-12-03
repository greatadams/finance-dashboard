package com.pm.greatadamu.transactionservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private BigDecimal amount;

    private Long fromAccountId;

    private Long toAccountId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private LocalDateTime transactionDate;
}

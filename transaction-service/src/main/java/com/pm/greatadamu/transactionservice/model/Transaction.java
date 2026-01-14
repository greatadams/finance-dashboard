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
@Table(name = "transactions",
        indexes = {
                @Index(name = "idx_tx_customer_date", columnList = "customerId,transactionDate"),
                @Index(name = "idx_tx_from_account", columnList = "fromAccountId"),
                @Index(name = "idx_tx_to_account", columnList = "toAccountId"),
                @Index(name = "idx_tx_status", columnList = "transactionStatus")
        })
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String idempotencyKey;


    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private Long fromAccountId;

    private Long toAccountId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private LocalDateTime transactionDate;

    @PrePersist
    public void prePersist() {
        this.transactionDate = LocalDateTime.now();
    }

}

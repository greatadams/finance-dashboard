package com.pm.greatadamu.accountservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "accounts",
        indexes = {
                @Index(name = "idx_account_account_number", columnList = "accountNumber"),
                @Index(name = "idx_account_customer_id", columnList = "customerId")
        })
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Version //  optimistic locking
    private Long version;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false,precision=19,scale=2)
    private BigDecimal accountBalance;

    private String accountNumber;

    private String accountName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist//Quickly updated the timeStamp before it inserted db
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate//runs before an UPDATE
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}

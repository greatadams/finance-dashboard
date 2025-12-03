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
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private BigDecimal accountBalance;

    private String accountNumber;

    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

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

package com.pm.greatadamu.accountservice.dto;

import com.pm.greatadamu.accountservice.model.AccountStatus;
import com.pm.greatadamu.accountservice.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {
    private Long id;
    private Long customerId;
    private BigDecimal accountBalance;
    private String accountName;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

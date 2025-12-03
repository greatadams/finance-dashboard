package com.pm.greatadamu.accountservice.mapper;

import com.pm.greatadamu.accountservice.dto.AccountRequestDto;
import com.pm.greatadamu.accountservice.dto.AccountResponseDto;
import com.pm.greatadamu.accountservice.model.Account;
import com.pm.greatadamu.accountservice.model.AccountStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
//USER INPUT -> BUILD ACCOUNT ENTITY -> SAVE IN DB
public class AccountMapper {
    public Account mapToEntity(AccountRequestDto accountRequestDto) {
        return Account.builder()
                .customerId(accountRequestDto.getCustomerId())
                .accountName(accountRequestDto.getAccountName())
                .accountType(accountRequestDto.getAccountType())
                .accountBalance(BigDecimal.ZERO)
                .accountStatus(AccountStatus.ACTIVE)
                .accountNumber(generateAccountNumber())
                .build();
    }

    //WHEN USER SEE IN RETURN DTO BACK TO JSON
    public AccountResponseDto mapToResponse(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getCustomerId(),
                account.getAccountBalance(),
                account.getAccountName(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getAccountStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()

        );
    }

    private String generateAccountNumber() {
        return "ACC-" + System.currentTimeMillis();
    }
}

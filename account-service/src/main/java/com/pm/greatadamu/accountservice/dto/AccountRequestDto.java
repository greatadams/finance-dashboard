package com.pm.greatadamu.accountservice.dto;

import com.pm.greatadamu.accountservice.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto {
    @NotNull
    @Positive
    private Long customerId;

    @NotBlank
    @Size(min = 1, max = 100)
    private String accountName;

    @NotNull
    private AccountType accountType;
}

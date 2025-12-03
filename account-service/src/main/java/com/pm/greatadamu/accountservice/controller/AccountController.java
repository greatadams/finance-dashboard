package com.pm.greatadamu.accountservice.controller;

import com.pm.greatadamu.accountservice.dto.AccountRequestDto;
import com.pm.greatadamu.accountservice.dto.AccountResponseDto;
import com.pm.greatadamu.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @RequestBody @Valid
            AccountRequestDto accountRequestDto)
    {
        //call the service to get the user input->saved to db->send back to user as json to view
        AccountResponseDto accountResponseDto = accountService.createAccount(accountRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @PathVariable Long id,
            @RequestBody @Valid
            AccountRequestDto accountRequestDto){
        //call the service to get the user input and update account
        AccountResponseDto accountResponseDto = accountService.updateAccount(id,accountRequestDto);
        return ResponseEntity.ok(accountResponseDto);

    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> getAccountByAccountNumber(
            @PathVariable("accountNumber")
            String accountNumber) {
        //call the service to get  the response DTO(what user will see)
       AccountResponseDto responseDto = accountService.getAccountByAccountNumber(accountNumber);
       //response dto as json in the HTTP 200 ok response
       return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccountByAccountNumber(@PathVariable  String accountNumber) {
        accountService.deleteAccountByAccountNumber(accountNumber);
        return ResponseEntity.noContent().build();
    }
}

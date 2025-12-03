package com.pm.greatadamu.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("Account with number " + accountNumber + " not found");
    }
}

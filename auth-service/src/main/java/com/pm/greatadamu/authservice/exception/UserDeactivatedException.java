package com.pm.greatadamu.authservice.exception;

public class UserDeactivatedException extends RuntimeException {
    public UserDeactivatedException(String message) {
        super(message);
    }
}

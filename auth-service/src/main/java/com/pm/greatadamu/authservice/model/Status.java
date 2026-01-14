package com.pm.greatadamu.authservice.model;

public enum Status {
    ACTIVE,
    PENDING_VERIFICATION,//REG BUT EMAIL NOT VERIFIED
    LOCKED, //TOO MANY FAILED ATTEMPTS
    DISABLED, //ADMIN DISABLED
    DELETED //SOFT DELETE
}

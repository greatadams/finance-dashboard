package com.pm.greatadamu.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users",
indexes = {
        @Index(name = "idx_users_email_normalized",columnList = "email_normalized")

}
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Column(nullable = false, length=320)
    private String email;

    @Column(name = "email_normalized", unique = true, nullable = false,length = 320)
    private String emailNormalized;


    @Column(nullable = false)
    private String passwordHash;

    private Instant passwordChangedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private Instant lastLogin;

    private int failedLoginAttempt;

    private Instant lockedUntil;//needed for lockout

    private Instant created;

    private Instant updated;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        created = now;
        updated = now;
        emailNormalized=normalizeEmail(email);
    }

    @PreUpdate
    public void preUpdate() {
        Instant now = Instant.now();
        updated = now;
        emailNormalized=normalizeEmail(email);
    }

   private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}

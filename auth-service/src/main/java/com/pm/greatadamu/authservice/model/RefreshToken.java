package com.pm.greatadamu.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="refresh_tokens",indexes = {
        @Index(name = "idx_refresh_token_user_id",columnList = "user_id"),
        @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash")
})
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Which user this refresh token belongs to
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

   // Store HASHED refresh token (never store raw)
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

   // Expiration + revocation timestamps (real-world)
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    //Marks token as explicitly invalid
    @Column(name = "revoked_at")
    private Instant revokedAt;

    //When this session was created
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
   public void prePersist() {
        createdAt = Instant.now();
    }

}

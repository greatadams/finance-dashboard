package com.pm.greatadamu.authservice.repository;

import com.pm.greatadamu.authservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    //find token row by its hash
    Optional<RefreshToken> findByTokenHash(String tokenHash);

   // revoke all active tokens for user (single-session + reuse protection)
    @Modifying
    @Query("""
        update RefreshToken rt
        set rt.revokedAt = :now
        where rt.user.id = :userId
          and rt.revokedAt is null
          and rt.expiresAt > :now
    """)
    int revokeAllActiveByUserId(@Param("userId") Long userId, @Param("now") Instant now);

    //revoke helper
    long deleteByExpiresAtBefore(Instant now);
}

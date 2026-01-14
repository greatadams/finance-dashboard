package com.pm.greatadamu.authservice.jwtUtil;

import com.pm.greatadamu.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

   //access token lifetime
    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    //identifies your auth service as the token issuer
    @Value("${jwt.issuer}")
    private String issuer;

    public long getExpirationMs() {
        return expirationMs;
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    // Generate access token with RSA
    //created on login (and on refresh).
    public String generateToken(User user) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expirationAt=Date.from(now.plusMillis(expirationMs));

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email",user.getEmail())
                .claim("customerId",user.getCustomerId())
                .claim("role",user.getRole().name())
                .claim("roles", List.of(user.getRole().name())) //for gateway compatibility
                .setIssuer(issuer)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationAt)
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
    // Generate refresh token
    //created on login, and created again on refresh if you rotate.
    public String generateRefreshToken(User user, long refreshExpirationMs) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expirationAt = Date.from(now.plusMillis(refreshExpirationMs));

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("type", "refresh")
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiration(expirationAt)
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    //quick validation check.
    public boolean isTokenValid(String token) {
        try{
            Claims claims = extractAllClaims(token);
            boolean notExpired=claims.getExpiration().after(new Date());
            boolean issuerMatches=issuer.equals(claims.getIssuer());

            return notExpired && issuerMatches ;
        }catch (Exception e){
            return false;
        }
    }


    //quick validation check for refresh token.
    public  boolean validateRefreshToken(String token) {
        try{
            Claims claims = extractAllClaims(token);
            String tokenType=claims.get("type",String.class);
            if (!"refresh".equals(tokenType)) {
                return false;
            }

            boolean notExpired = claims.getExpiration().after(new Date());
            boolean issuerMatches = issuer.equals(claims.getIssuer());

            return notExpired && issuerMatches;

        }catch (Exception e){
            return false;
        }
    }

    public Long extractUserId(String token) {
        String sub = extractClaim(token,Claims::getSubject);
        return Long.parseLong(sub);
    }

    public Long extractCustomerId(String token) {
        return extractClaim(token,claims -> {
            Number n =claims.get("customerId",Number.class);
            return n==null?null:n.longValue();
        });
    }

    public String extractEmail(String token) {
        return extractClaim(token,claims -> claims.get("email",String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Instant extractIssuedAt(String token) {
        Date issuedAt = extractClaim(token,Claims::getIssuedAt);
        return issuedAt == null ? null : issuedAt.toInstant();
    }

    //INTERNAL HELPERS
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                        .verifyWith(publicKey)
                        .clockSkewSeconds(60)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
    }
}

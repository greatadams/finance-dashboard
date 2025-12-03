package com.pm.greatadamu.authservice.jwtUtil;

import com.pm.greatadamu.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.expiration-ms}")
    private Long expirationMs;

    @Value("${jwt.secret}")
    private String secret;

    public long getExpirationMs() {
        return expirationMs;
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    @Value("${jwt.issuer}")
    private String issuer;

    //Build signing key from secret
    private Key getSigningKey() {
        //plain string → bytes → HMAC key
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    //PUBLIC API
    public String generateToken(User user) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expirationAt=Date.from(now.plusMillis(expirationMs));

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email",user.getEmail())
                .claim("customerId",user.getCustomerId())
                .claim("role",user.getRole().name())
                .setIssuer(issuer)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationAt)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try{
            Claims claims = extractAllClaims(token);
            Date expiration=claims.getExpiration();
            return expiration.after(new Date()) ;
        }catch (Exception e){
            return false;
        }
    }

    public Long extractUserId(String token) {
        String sub = extractClaim(token,Claims::getSubject);
        return Long.parseLong(sub);
    }

    public Long extractCustomerId(String token) {
        return extractClaim(token,claims -> claims.get("customerId",Long.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token,claims -> claims.get("email",String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    //INTERNAL HELPERS
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }
}

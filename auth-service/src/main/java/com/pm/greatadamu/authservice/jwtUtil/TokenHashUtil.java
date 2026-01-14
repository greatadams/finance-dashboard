package com.pm.greatadamu.authservice.jwtUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class TokenHashUtil {
    private TokenHashUtil() {}

    public static String hashRefreshToken(String rawToken) {
        try {
            //Hash using SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //Convert to bytes (UTF-8)
            byte[] digest = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            //Convert bytes → hex string
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        }catch (Exception e) {
            throw new RuntimeException("Failed to hash token",e);
        }
    }
}

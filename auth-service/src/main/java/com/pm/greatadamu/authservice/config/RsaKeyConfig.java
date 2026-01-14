package com.pm.greatadamu.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// NOTE: RSA keys are generated at startup.
// In production, load keys from a persistent keystore or secrets manager.

@Configuration
public class RsaKeyConfig {

    // Purpose:
    // Generates a 2048-bit RSA key pair
    //Used by JWT RS256 signing
    //Important property:
    //This key pair is generated at application startup
    @Bean
    public KeyPair keyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    //Purpose:
    // Exposes the public key
    //  Used by:
    //  JwtService to verify tokens
    //  JwksController to publish JWKS
    @Bean
    public RSAPublicKey publicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

   // Purpose:
    //Exposes the private key
    //Used by JwtService to sign access + refresh tokens
    @Bean
    public RSAPrivateKey privateKey(KeyPair keyPair) {
        return (RSAPrivateKey) keyPair.getPrivate();
    }
}

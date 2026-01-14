package com.pm.greatadamu.authservice.controller;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final RSAPublicKey rsaPublicKey;

    @Value("${jwt.kid:main-key}")
    private String keyId;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwk() {
        // Create a JWK from the RSA public key
        JWK jwk = new RSAKey.Builder(rsaPublicKey)
                .keyID(keyId)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
                .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}

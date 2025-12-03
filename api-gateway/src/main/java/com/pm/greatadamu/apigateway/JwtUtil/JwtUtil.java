package com.pm.greatadamu.apigateway.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
}

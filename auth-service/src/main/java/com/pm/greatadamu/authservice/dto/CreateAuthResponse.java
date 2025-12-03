package com.pm.greatadamu.authservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAuthResponse {
    String accessToken;
    String tokenType;
    private Long userId;
    private Long customerId;
    private String email;
    private String role;
    private Long expiresIn;
}

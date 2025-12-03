package com.pm.greatadamu.authservice.controller;

import com.pm.greatadamu.authservice.dto.CreateAuthResponse;
import com.pm.greatadamu.authservice.dto.CreateUserLoginDTO;
import com.pm.greatadamu.authservice.dto.CreateUserRegistrationRequestDTO;
import com.pm.greatadamu.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity <Void> register(
            @Valid @RequestBody CreateUserRegistrationRequestDTO createUserRegistrationRequestDTO) {
        authService.register(createUserRegistrationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/login")
    public ResponseEntity <CreateAuthResponse> login(
            @Valid  @RequestBody CreateUserLoginDTO createUserLoginDTO) {
       CreateAuthResponse response = authService.login(createUserLoginDTO);
        return ResponseEntity.ok(response);
    }
}

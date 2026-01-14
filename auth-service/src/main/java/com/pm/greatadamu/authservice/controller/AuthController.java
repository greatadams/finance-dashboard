package com.pm.greatadamu.authservice.controller;

import com.pm.greatadamu.authservice.dto.CreateAuthResponse;
import com.pm.greatadamu.authservice.dto.CreateUserLoginDTO;
import com.pm.greatadamu.authservice.dto.CreateUserRegistrationRequestDTO;
import com.pm.greatadamu.authservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Value("${auth.refresh.cookie-name}")
    private String refreshCookieName;

    @Value("${auth.refresh.expiration-seconds}")
    private long refreshExpirationSeconds;

    @Value("${auth.refresh.secure}")
    private boolean refreshCookieSecure;

    @Value("${auth.refresh.same-site}")
    private String refreshCookieSameSite;


    @PostMapping("/register")
    public ResponseEntity <Void> register(
            @Valid @RequestBody CreateUserRegistrationRequestDTO createUserRegistrationRequestDTO) {
        authService.register(createUserRegistrationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PostMapping("/login")
    public ResponseEntity <CreateAuthResponse> login(
            @Valid  @RequestBody CreateUserLoginDTO createUserLoginDTO,
            HttpServletResponse response
    ) {
        AuthService.LoginResult result = authService.loginWithRefresh(createUserLoginDTO);

        setRefreshCookie(response, result.refreshToken()); //set cookie
        return ResponseEntity.ok(result.response());
    }

    //refresh endpoint reads refresh cookie, returns new access token + rotates cookie
    @PostMapping("/refresh")
    public ResponseEntity<CreateAuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response

    ){
        String refreshToken = readCookie(request,refreshCookieName);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthService.LoginResult result = authService.refreshAccessToken(refreshToken);

        setRefreshCookie(response, result.refreshToken());
        return ResponseEntity.ok(result.response());
    }



    @PostMapping("/logout")
    public ResponseEntity <Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        String refreshToken =readCookie(request,refreshCookieName);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        authService.logout(refreshToken); //revoke in DB
        clearRefreshCookie(response); //remove cookie
        return ResponseEntity.noContent().build();
    }

    //  helper to set refresh token cookie (HttpOnly)
    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshToken)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path("/api/auth")
                .maxAge(refreshExpirationSeconds)
                .sameSite(refreshCookieSameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    //  helper to clear cookie on logout
    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path("/api/auth")
                .maxAge(0)
                .sameSite(refreshCookieSameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    //  helper to read cookie by name
    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}

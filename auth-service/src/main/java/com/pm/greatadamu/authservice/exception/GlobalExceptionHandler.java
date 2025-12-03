package com.pm.greatadamu.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;


@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1) Validation errors (DTO @Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err ->err.getField() + ": " +  err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse body =ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .code("VALIDATION_ERROR")
                .build();

        return  ResponseEntity.badRequest().body(body);
    }

    //handle invalid json request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException ex,
                                                       HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Malformed JSON request")
                .path(request.getRequestURI())
                .code("MALFORMED_JSON")
                .build();

        return ResponseEntity.badRequest().body(body);
    }
    // 2) User already exists
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                                 HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code("USER_ALREADY_EXISTS")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 3) User deactivated
    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserDeactivated(UserDeactivatedException ex,
                                                               HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code("USER_DEACTIVATED")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
    //handle invalid credentials
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                  HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code("INVALID_CREDENTIALS")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
    // 4) Fallback for any other unhandled exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex,
                                                   HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error occurred")
                .path(request.getRequestURI())
                .code("INTERNAL_ERROR")
                .build();

        ex.printStackTrace(); // or use a logger

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


}

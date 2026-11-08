package com.example.blog.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.domain.dtos.ApiErrorResponse;

@RestController
@ControllerAdvice 
public class ErrorController {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .message("Invalid email or password")
                .status(401)
                .build();
        return ResponseEntity.status(401).body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .message(ex.getMessage())
                .status(400)
                .build();
        return ResponseEntity.status(400).body(apiResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .message(ex.getMessage())
                .status(400)
                .build();
        return ResponseEntity.status(400).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ApiErrorResponse.ErrorField> errorFields = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiErrorResponse.ErrorField(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .message("Validation failed")
                .status(400)
                .errors(errorFields)
                .build();
        return ResponseEntity.status(400).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex) {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .message(ex.getMessage())
                .status(500)
                .build();
        return ResponseEntity.status(500).body(apiResponse);
    }
}

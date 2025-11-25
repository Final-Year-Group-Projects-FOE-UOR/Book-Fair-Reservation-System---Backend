package com.bookfair.bookfairreservationsystembackend.exception;

import com.bookfair.bookfairreservationsystembackend.dtos.response.ApiResponse;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(NotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(404).body(
                new ApiResponse(false, ex.getMessage(), null)
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(
                new ApiResponse(false, ex.getMessage(), null)
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorized(UnauthorizedException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(401).body(
                new ApiResponse(false, ex.getMessage(), null)
        );
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> handleValidation(ValidationException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(
                new ApiResponse(false, ex.getMessage(), null)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(
                new ApiResponse(false, "Validation failed", errors)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleJsonErrors(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON format";
        if (ex.getCause() instanceof JsonParseException jsonEx) {
            message = "JSON parsing error: " + jsonEx.getOriginalMessage();
        }
        log.error("JSON error: {}", message);
        return ResponseEntity.badRequest().body(
                new ApiResponse(false, message, null)
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied to URL {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(403)
                .body(new ApiResponse(false, "You do not have permission to access this resource", null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failed for URL {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(401)
                .body(new ApiResponse(false, "Authentication failed: " + ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity.status(401)
                .body(new ApiResponse(false, "Invalid username or password", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on URL {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(new ApiResponse(false, "Internal server error", null));
    }
}




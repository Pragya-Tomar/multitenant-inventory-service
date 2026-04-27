package com.inventory.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exceptions.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(Exceptions.ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.TenantAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(Exceptions.TenantAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exceptions.InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(Exceptions.InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(400, "Validation failed", errors));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access denied"));
    }

    public record ErrorResponse(int status, String message, Instant timestamp) {
        public ErrorResponse(int status, String message) {
            this(status, message, Instant.now());
        }
    }

    public record ValidationErrorResponse(int status, String message,
                                          Map<String, String> errors, Instant timestamp) {
        public ValidationErrorResponse(int status, String message, Map<String, String> errors) {
            this(status, message, errors, Instant.now());
        }
    }
}

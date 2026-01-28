package com.elowen.admin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for admin-service.
 * 
 * UI-FRIENDLY ERROR RESPONSES:
 * - Consistent error format across all endpoints
 * - Proper HTTP status codes
 * - Clean error messages without internal details
 * - Validation error details for form fields
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle access denied (403) - Non-admin users
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Access Denied");
        error.put("message", "Admin privileges required");
        error.put("status", HttpStatus.FORBIDDEN.value());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handle brand not found (404) - Wrong client or non-existent brand
     */
    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBrandNotFound(BrandNotFoundException e) {
        log.warn("Brand not found: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Brand Not Found");
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.NOT_FOUND.value());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handle duplicate brand name (409) - Name conflict within client
     */
    @ExceptionHandler(BrandNameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleBrandNameExists(BrandNameAlreadyExistsException e) {
        log.warn("Brand name conflict: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Brand Name Already Exists");
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.CONFLICT.value());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    /**
     * Handle validation errors (400) - Invalid request data
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        // Extract field-specific validation errors
        e.getBindingResult().getAllErrors().forEach(err -> {
            if (err instanceof FieldError) {
                FieldError fieldError = (FieldError) err;
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        });
        
        error.put("error", "Validation Failed");
        error.put("message", "Invalid request data");
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle illegal argument (400) - Business rule violations
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Invalid Request");
        error.put("message", e.getMessage());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle illegal state (500) - Internal server errors
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        log.error("Illegal state: {}", e.getMessage(), e);
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An internal error occurred");
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Handle generic exceptions (500) - Unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred");
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
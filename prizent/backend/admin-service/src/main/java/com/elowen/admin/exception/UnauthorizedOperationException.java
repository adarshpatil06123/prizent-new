package com.elowen.admin.exception;

/**
 * Exception thrown when a user attempts to perform an operation 
 * they don't have sufficient privileges for.
 * 
 * This is specifically for role-based authorization failures.
 */
public class UnauthorizedOperationException extends RuntimeException {
    
    public UnauthorizedOperationException(String message) {
        super(message);
    }
    
    public UnauthorizedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
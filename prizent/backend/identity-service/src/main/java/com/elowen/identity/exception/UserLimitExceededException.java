package com.elowen.identity.exception;

/**
 * Exception thrown when user creation would exceed the client's user limit
 */
public class UserLimitExceededException extends RuntimeException {
    
    public UserLimitExceededException(String message) {
        super(message);
    }
    
    public UserLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
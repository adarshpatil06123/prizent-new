package com.elowen.admin.entity;

/**
 * User roles for authorization and access control.
 * 
 * ROLE HIERARCHY:
 * - ADMIN: Full administrative access, can manage all resources
 * - USER: Standard user access, limited permissions
 * 
 * ARCHITECTURAL NOTE:
 * This enum is duplicated across services to maintain microservice independence.
 * Each service maintains its own copy to avoid cross-service dependencies.
 */
public enum Role {
    ADMIN("ADMIN"),
    USER("USER");
    
    private final String value;
    
    Role(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
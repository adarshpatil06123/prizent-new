package com.elowen.admin.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to create a brand with a name 
 * that already exists for the client.
 * 
 * Enforces business rule: (client_id, name) must be unique
 */
public class BrandNameAlreadyExistsException extends RuntimeException {
    
    private final String brandName;
    private final UUID clientId;
    
    public BrandNameAlreadyExistsException(String brandName, UUID clientId) {
        super(String.format("Brand with name '%s' already exists for client %s", brandName, clientId));
        this.brandName = brandName;
        this.clientId = clientId;
    }
    
    public String getBrandName() {
        return brandName;
    }
    
    public UUID getClientId() {
        return clientId;
    }
}
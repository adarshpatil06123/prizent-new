package com.elowen.admin.exception;

import java.util.UUID;

/**
 * Exception thrown when a brand is not found within client's tenant boundary.
 * 
 * This exception maintains tenant security by not revealing whether:
 * - The brand doesn't exist at all, OR
 * - The brand exists but belongs to another client
 * 
 * Both scenarios result in the same "not found" response.
 */
public class BrandNotFoundException extends RuntimeException {
    
    private final UUID brandId;
    private final UUID clientId;
    
    public BrandNotFoundException(UUID brandId, UUID clientId) {
        super(String.format("Brand with ID %s not found for client %s", brandId, clientId));
        this.brandId = brandId;
        this.clientId = clientId;
    }
    
    public BrandNotFoundException(String message) {
        super(message);
        this.brandId = null;
        this.clientId = null;
    }
    
    public UUID getBrandId() {
        return brandId;
    }
    
    public UUID getClientId() {
        return clientId;
    }
}
package com.elowen.admin.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing brand.
 * 
 * UI-ALIGNED Rules:
 * - NO client_id field - immutable and from JWT
 * - NO enabled field - use separate enable/disable endpoints
 * - NO timestamp fields - managed by JPA
 * 
 * Update Rules:
 * - All fields are optional (partial updates allowed)
 * - Only name, description, and logo can be updated
 * - Null values mean "do not update this field"
 * - When name IS provided, it cannot be blank (UI validation backup)
 */
public class UpdateBrandRequest {
    
    @Size(min = 1, max = 100, message = "Brand name must be between 1 and 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Size(max = 255, message = "Logo URL cannot exceed 255 characters")
    private String logo;
    
    // Constructors
    public UpdateBrandRequest() {}
    
    public UpdateBrandRequest(String name, String description, String logo) {
        this.name = name;
        this.description = description;
        this.logo = logo;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLogo() {
        return logo;
    }
    
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    /**
     * Utility method to check if name should be updated
     */
    public boolean hasName() {
        return name != null;
    }
    
    /**
     * Utility method to check if description should be updated
     */
    public boolean hasDescription() {
        return description != null;
    }
    
    /**
     * Utility method to check if logo should be updated
     */
    public boolean hasLogo() {
        return logo != null;
    }
    
    /**
     * Check if request has any fields to update
     */
    public boolean isEmpty() {
        return name == null && description == null && logo == null;
    }
    
    @Override
    public String toString() {
        return "UpdateBrandRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
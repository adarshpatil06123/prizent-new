package com.elowen.admin.dto;

import com.elowen.admin.entity.Brand;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Brand operations.
 * 
 * UI-ALIGNED Design:
 * - Contains only fields needed by UI: id, name, description, logo, enabled
 * - NO client_id exposure (security best practice)
 * - Minimal timestamps for UI display
 * - Optimized for frontend consumption
 */
public class BrandResponse {
    
    private UUID id;
    private String name;
    private String description;
    private String logo;
    private Boolean enabled;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
    
    // Constructors
    public BrandResponse() {}
    
    public BrandResponse(UUID id, String name, String description, 
                        String logo, Boolean enabled, LocalDateTime createDateTime, 
                        LocalDateTime updateDateTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.enabled = enabled;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }
    
    /**
     * Factory method to create response from Brand entity
     * UI-OPTIMIZED: Only includes fields needed by frontend
     */
    public static BrandResponse fromEntity(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getDescription(),
            brand.getLogo(),
            brand.getEnabled(),
            brand.getCreateDateTime(),
            brand.getUpdateDateTime()
        );
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

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
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }
    
    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }
    
    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }
    
    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
    
    @Override
    public String toString() {
        return "BrandResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                ", enabled=" + enabled +
                ", createDateTime=" + createDateTime +
                ", updateDateTime=" + updateDateTime +
                '}';
    }
}
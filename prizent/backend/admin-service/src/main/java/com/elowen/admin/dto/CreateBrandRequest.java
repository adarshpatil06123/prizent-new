package com.elowen.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new brand.
 * 
 * SECURITY RULES:
 * - NO client_id field - always extracted from JWT
 * - NO enabled field - always defaults to true
 * - NO timestamp fields - managed by JPA
 * 
 * Validation Rules:
 * - name is required and cannot be blank
 * - description is optional but limited in length
 * - logo is optional URL/path string
 */
public class CreateBrandRequest {
    
    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Size(max = 255, message = "Logo URL cannot exceed 255 characters")
    private String logo;
    
    // Constructors
    public CreateBrandRequest() {}
    
    public CreateBrandRequest(String name, String description, String logo) {
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
    
    @Override
    public String toString() {
        return "CreateBrandRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
package com.elowen.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Brand entity representing client-owned brands in the system.
 * 
 * Key Design Decisions:
 * - Uses UUID for id to ensure global uniqueness across distributed system
 * - client_id is immutable and extracted from UserPrincipal - never from request
 * - Composite unique constraint on (client_id, name) for tenant isolation
 * - Soft delete pattern using 'enabled' field - no physical deletion
 * - Automatic timestamp management with JPA annotations
 * 
 * PRODUCT INTEGRATION READINESS:
 * - Entity kept free of JPA relationships to future Product entities
 * - Disable logic prepared for future product reference validation
 * - No premature optimization for cross-entity operations
 * - Brand lifecycle managed independently of product associations
 */
@Entity
@Table(
    name = "p_brands",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_brands_client_name", 
            columnNames = {"client_id", "name"}
        )
    },
    indexes = {
        @Index(name = "idx_brands_client_id", columnList = "client_id"),
        @Index(name = "idx_brands_enabled", columnList = "enabled")
    }
)
public class Brand {
    
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * Client ID - IMMUTABLE after creation
     * Always extracted from UserPrincipal, never from request
     */
    @NotNull
    @Column(name = "client_id", updatable = false, nullable = false)
    private UUID clientId;
    
    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "logo", length = 255)
    private String logo;
    
    /**
     * Soft delete flag - true = active, false = disabled/soft-deleted
     * Default: true (active)
     */
    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @CreationTimestamp
    @Column(name = "create_date_time", updatable = false, nullable = false)
    private LocalDateTime createDateTime;
    
    @UpdateTimestamp
    @Column(name = "update_date_time", nullable = false)
    private LocalDateTime updateDateTime;
    
    // Constructors
    public Brand() {}
    
    public Brand(UUID clientId, String name, String description, String logo) {
        this.clientId = clientId;
        this.name = name;
        this.description = description;
        this.logo = logo;
        this.enabled = true;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getClientId() {
        return clientId;
    }
    
    /**
     * Client ID setter - should only be used during entity creation
     * In production, this comes from JWT and should be immutable
     */
    public void setClientId(UUID clientId) {
        this.clientId = clientId;
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
        return "Brand{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                ", enabled=" + enabled +
                ", createDateTime=" + createDateTime +
                ", updateDateTime=" + updateDateTime +
                '}';
    }
}
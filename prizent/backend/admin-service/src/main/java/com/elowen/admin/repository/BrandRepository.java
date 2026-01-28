package com.elowen.admin.repository;

import com.elowen.admin.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Brand entities with STRICT tenant isolation.
 * 
 * CRITICAL SECURITY RULES:
 * - ALL queries MUST include client_id for tenant safety
 * - NO findById() or findByName() methods without tenant isolation
 * - ALL operations are scoped to the authenticated client
 * 
 * Design Decisions:
 * - Custom queries ensure tenant isolation cannot be bypassed
 * - Explicit parameter naming for clarity and security
 * - Separation of active vs all brands for business logic flexibility
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    
    /**
     * Find brand by ID within client's tenant boundary
     * SECURITY: Prevents cross-tenant data access
     */
    Optional<Brand> findByIdAndClientId(UUID id, UUID clientId);
    
    /**
     * Find all brands for a specific client (including disabled)
     * Useful for admin operations that need to see all brands
     */
    List<Brand> findAllByClientIdOrderByCreateDateTimeDesc(UUID clientId);
    
    /**
     * Find only active (enabled) brands for a client
     * Most common operation for business logic
     */
    List<Brand> findAllByClientIdAndEnabledTrueOrderByCreateDateTimeDesc(UUID clientId);
    
    /**
     * Check if brand name exists for client (case-insensitive)
     * Used for validation during create/update operations
     */
    boolean existsByClientIdAndNameIgnoreCase(UUID clientId, String name);
    
    /**
     * Check if brand name exists for client excluding specific brand ID
     * Used during update operations to allow same name for same brand
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Brand b " +
           "WHERE b.clientId = :clientId " +
           "AND LOWER(b.name) = LOWER(:name) " +
           "AND b.id != :excludeId")
    boolean existsByClientIdAndNameIgnoreCaseAndIdNot(
        @Param("clientId") UUID clientId, 
        @Param("name") String name, 
        @Param("excludeId") UUID excludeId
    );
    
    /**
     * Find active brand by name (case-insensitive) within client tenant
     * Used for lookups and validation
     */
    Optional<Brand> findByClientIdAndNameIgnoreCaseAndEnabledTrue(UUID clientId, String name);
}
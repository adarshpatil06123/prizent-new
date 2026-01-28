package com.elowen.admin.service;

import com.elowen.admin.dto.BrandResponse;
import com.elowen.admin.dto.CreateBrandRequest;
import com.elowen.admin.dto.UpdateBrandRequest;
import com.elowen.admin.entity.Brand;
import com.elowen.admin.exception.BrandNameAlreadyExistsException;
import com.elowen.admin.exception.BrandNotFoundException;
import com.elowen.admin.repository.BrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for Brand management with strict tenant isolation.
 * 
 * CRITICAL SECURITY PRINCIPLES:
 * - ALL operations are scoped by client_id from UserPrincipal
 * - NO operation can access brands from other clients
 * - client_id is NEVER accepted from request parameters
 * 
 * Business Rules Enforced:
 * - Brand names must be unique per client (case-insensitive)
 * - Soft delete only - no physical deletion
 * - Brands default to enabled=true on creation
 * - Future product relationship validation on disable
 */
@Service
public class BrandService {
    
    private static final Logger log = LoggerFactory.getLogger(BrandService.class);
    
    private final BrandRepository brandRepository;
    
    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }
    
    /**
     * Create a new brand for the authenticated client.
     * 
     * Business Rules:
     * - Name must be unique per client (case-insensitive)
     * - Brand is enabled by default
     * - client_id comes from authentication context
     */
    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request, UUID clientId) {
        log.info("Creating brand '{}' for client {}", request.getName(), clientId);
        
        // Validate name uniqueness
        if (brandRepository.existsByClientIdAndNameIgnoreCase(clientId, request.getName())) {
            log.warn("Brand creation failed - name '{}' already exists for client {}", 
                    request.getName(), clientId);
            throw new BrandNameAlreadyExistsException(request.getName(), clientId);
        }
        
        // Create brand entity
        Brand brand = new Brand(
            clientId,
            request.getName().trim(),
            StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null,
            StringUtils.hasText(request.getLogo()) ? request.getLogo().trim() : null
        );
        
        // Save and return response
        Brand savedBrand = brandRepository.save(brand);
        
        log.info("Successfully created brand with ID {} for client {}", 
                savedBrand.getId(), clientId);
        
        return BrandResponse.fromEntity(savedBrand);
    }
    
    /**
     * Get all brands for the authenticated client.
     * Returns both enabled and disabled brands for admin view.
     */
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands(UUID clientId) {
        log.debug("Fetching all brands for client {}", clientId);
        
        List<Brand> brands = brandRepository.findAllByClientIdOrderByCreateDateTimeDesc(clientId);
        
        log.debug("Found {} brands for client {}", brands.size(), clientId);
        
        return brands.stream()
                .map(BrandResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Get only active (enabled) brands for the authenticated client.
     * Most common operation for business logic.
     */
    @Transactional(readOnly = true)
    public List<BrandResponse> getActiveBrands(UUID clientId) {
        log.debug("Fetching active brands for client {}", clientId);
        
        List<Brand> brands = brandRepository.findAllByClientIdAndEnabledTrueOrderByCreateDateTimeDesc(clientId);
        
        log.debug("Found {} active brands for client {}", brands.size(), clientId);
        
        return brands.stream()
                .map(BrandResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Get specific brand by ID within client's tenant boundary.
     * 
     * Security: Ensures client can only access their own brands
     */
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(UUID brandId, UUID clientId) {
        log.debug("Fetching brand {} for client {}", brandId, clientId);
        
        Brand brand = brandRepository.findByIdAndClientId(brandId, clientId)
                .orElseThrow(() -> {
                    log.warn("Brand {} not found for client {}", brandId, clientId);
                    return new BrandNotFoundException(brandId, clientId);
                });
        
        return BrandResponse.fromEntity(brand);
    }
    
    /**
     * Update existing brand within client's tenant boundary.
     * 
     * Business Rules:
     * - Only name, description, and logo can be updated
     * - Name uniqueness validation (excluding current brand)
     * - Partial updates supported (null fields are ignored)
     */
    @Transactional
    public BrandResponse updateBrand(UUID brandId, UpdateBrandRequest request, UUID clientId) {
        log.info("Updating brand {} for client {}", brandId, clientId);
        
        // Check if request has any updates
        if (request.isEmpty()) {
            log.debug("Update request is empty for brand {}", brandId);
            return getBrandById(brandId, clientId); // Return current state
        }
        
        // Fetch existing brand with tenant isolation
        Brand existingBrand = brandRepository.findByIdAndClientId(brandId, clientId)
                .orElseThrow(() -> {
                    log.warn("Brand {} not found for client {} during update", brandId, clientId);
                    return new BrandNotFoundException(brandId, clientId);
                });
        
        // Validate name uniqueness if name is being updated
        if (request.hasName() && !request.getName().trim().equalsIgnoreCase(existingBrand.getName())) {
            if (brandRepository.existsByClientIdAndNameIgnoreCaseAndIdNot(clientId, request.getName(), brandId)) {
                log.warn("Brand update failed - name '{}' already exists for client {}", 
                        request.getName(), clientId);
                throw new BrandNameAlreadyExistsException(request.getName(), clientId);
            }
        }
        
        // Apply updates
        if (request.hasName()) {
            existingBrand.setName(request.getName().trim());
        }
        
        if (request.hasDescription()) {
            existingBrand.setDescription(
                StringUtils.hasText(request.getDescription()) 
                    ? request.getDescription().trim() 
                    : null
            );
        }
        
        if (request.hasLogo()) {
            existingBrand.setLogo(
                StringUtils.hasText(request.getLogo()) 
                    ? request.getLogo().trim() 
                    : null
            );
        }
        
        // Save and return response
        Brand updatedBrand = brandRepository.save(existingBrand);
        
        log.info("Successfully updated brand {} for client {}", brandId, clientId);
        
        return BrandResponse.fromEntity(updatedBrand);
    }
    
    /**
     * Enable (activate) a brand - soft undelete operation.
     */
    @Transactional
    public BrandResponse enableBrand(UUID brandId, UUID clientId) {
        log.info("Enabling brand {} for client {}", brandId, clientId);
        
        Brand brand = brandRepository.findByIdAndClientId(brandId, clientId)
                .orElseThrow(() -> {
                    log.warn("Brand {} not found for client {} during enable", brandId, clientId);
                    return new BrandNotFoundException(brandId, clientId);
                });
        
        if (Boolean.TRUE.equals(brand.getEnabled())) {
            log.debug("Brand {} is already enabled for client {}", brandId, clientId);
        } else {
            brand.setEnabled(true);
            brandRepository.save(brand);
            log.info("Successfully enabled brand {} for client {}", brandId, clientId);
        }
        
        return BrandResponse.fromEntity(brand);
    }
    
    /**
     * Disable (soft delete) a brand.
     * Physical deletion is NOT allowed per business rules.
     * 
     * TODO: Before disabling, validate that no products are mapped to this brand.
     * When Product module is implemented, add validation to prevent disabling
     * brands that have active product relationships.
     */
    @Transactional
    public BrandResponse disableBrand(UUID brandId, UUID clientId) {
        log.info("Disabling brand {} for client {}", brandId, clientId);
        
        Brand brand = brandRepository.findByIdAndClientId(brandId, clientId)
                .orElseThrow(() -> {
                    log.warn("Brand {} not found for client {} during disable", brandId, clientId);
                    return new BrandNotFoundException(brandId, clientId);
                });
        
        // TODO: Add product validation here when Product module is implemented
        // validateNoActiveProductsForBrand(brandId, clientId);
        
        if (Boolean.FALSE.equals(brand.getEnabled())) {
            log.debug("Brand {} is already disabled for client {}", brandId, clientId);
        } else {
            brand.setEnabled(false);
            brandRepository.save(brand);
            log.info("Successfully disabled brand {} for client {}", brandId, clientId);
        }
        
        return BrandResponse.fromEntity(brand);
    }

    /**
     * Delete a brand permanently from the database.
     * 
     * SECURITY: Brand can only be deleted by same client that owns it
     * BUSINESS RULE: Hard delete - permanently removes brand from database
     * 
     * @param brandId UUID of the brand to delete
     * @param clientId UUID of the client (for security validation)
     * @throws BrandNotFoundException if brand doesn't exist or not owned by client
     */
    @Transactional
    public void deleteBrand(UUID brandId, UUID clientId) {
        log.debug("Attempting to delete brand {} for client {}", brandId, clientId);
        
        Brand brand = brandRepository.findByIdAndClientId(brandId, clientId)
            .orElseThrow(() -> {
                log.warn("Brand delete failed: brand {} not found for client {}", brandId, clientId);
                return new BrandNotFoundException("Brand not found or access denied");
            });

        // TODO: Add product validation here when Product module is implemented
        // validateNoActiveProductsForBrand(brandId, clientId);
        
        brandRepository.delete(brand);
        log.info("Successfully deleted brand {} ({}) for client {}", brandId, brand.getName(), clientId);
    }
}
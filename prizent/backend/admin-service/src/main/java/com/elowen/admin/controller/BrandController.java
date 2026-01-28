package com.elowen.admin.controller;

import com.elowen.admin.dto.BrandResponse;
import com.elowen.admin.dto.CreateBrandRequest;
import com.elowen.admin.dto.UpdateBrandRequest;
import com.elowen.admin.exception.BrandNameAlreadyExistsException;
import com.elowen.admin.exception.BrandNotFoundException;
import com.elowen.admin.security.UserPrincipal;
import com.elowen.admin.service.BrandService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Brand Management.
 * 
 * CRITICAL SECURITY RULES:
 * - ALL endpoints secured with @PreAuthorize("hasRole('ADMIN')")
 * - client_id NEVER accepted from request body or path parameters
 * - client_id ALWAYS extracted from authenticated UserPrincipal/JWT
 * - All operations are tenant-isolated
 * 
 * Base Path: /api/admin/brands
 * 
 * NOTE: This assumes the JWT contains client_id claim that can be extracted
 * from the UserPrincipal. Adjust getUserClientId() method based on your
 * JWT implementation in identity-service.
 */
@RestController
@RequestMapping("/api/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
public class BrandController {
    
    private static final Logger log = LoggerFactory.getLogger(BrandController.class);
    
    private final BrandService brandService;
    
    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }
    
    /**
     * Create a new brand
     * POST /api/admin/brands
     */
    @PostMapping
    public ResponseEntity<BrandResponse> createBrand(
            @Valid @RequestBody CreateBrandRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.info("Creating brand '{}' for client {}", request.getName(), clientId);
        
        try {
            BrandResponse response = brandService.createBrand(request, clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (BrandNameAlreadyExistsException e) {
            log.warn("Brand creation failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
        }
    }
    
    /**
     * Get all brands for authenticated client
     * GET /api/admin/brands
     * 
     * UI REQUIREMENT: Always returns ALL brands (enabled + disabled)
     * UI handles filtering if needed
     */
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.debug("Fetching all brands for client {}", clientId);
        
        List<BrandResponse> brands = brandService.getAllBrands(clientId);
            
        return ResponseEntity.ok(brands);
    }
    
    /**
     * Get specific brand by ID
     * GET /api/admin/brands/{brandId}
     */
    @GetMapping("/{brandId}")
    public ResponseEntity<BrandResponse> getBrandById(
            @PathVariable UUID brandId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.debug("Fetching brand {} for client {}", brandId, clientId);
        
        try {
            BrandResponse response = brandService.getBrandById(brandId, clientId);
            return ResponseEntity.ok(response);
            
        } catch (BrandNotFoundException e) {
            log.warn("Brand lookup failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
        }
    }
    
    /**
     * Update existing brand
     * PUT /api/admin/brands/{brandId}
     */
    @PutMapping("/{brandId}")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable UUID brandId,
            @Valid @RequestBody UpdateBrandRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.info("Updating brand {} for client {}", brandId, clientId);
        
        try {
            BrandResponse response = brandService.updateBrand(brandId, request, clientId);
            return ResponseEntity.ok(response);
            
        } catch (BrandNotFoundException e) {
            log.warn("Brand update failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
            
        } catch (BrandNameAlreadyExistsException e) {
            log.warn("Brand update failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
        }
    }
    
    /**
     * Enable brand (activate/undelete)
     * PATCH /api/admin/brands/{brandId}/enable
     */
    @PatchMapping("/{brandId}/enable")
    public ResponseEntity<BrandResponse> enableBrand(
            @PathVariable UUID brandId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.info("Enabling brand {} for client {}", brandId, clientId);
        
        try {
            BrandResponse response = brandService.enableBrand(brandId, clientId);
            return ResponseEntity.ok(response);
            
        } catch (BrandNotFoundException e) {
            log.warn("Brand enable failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
        }
    }
    
    /**
     * Disable brand (soft delete)
     * PATCH /api/admin/brands/{brandId}/disable
     */
    @PatchMapping("/{brandId}/disable")
    public ResponseEntity<BrandResponse> disableBrand(
            @PathVariable UUID brandId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.info("Disabling brand {} for client {}", brandId, clientId);
        
        try {
            BrandResponse response = brandService.disableBrand(brandId, clientId);
            return ResponseEntity.ok(response);
            
        } catch (BrandNotFoundException e) {
            log.warn("Brand disable failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
        }
    }

    /**
     * Delete a brand permanently
     * DELETE /api/admin/brands/{brandId}
     */
    @DeleteMapping("/{brandId}")
    public ResponseEntity<Map<String, String>> deleteBrand(
            @PathVariable UUID brandId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        UUID clientId = userPrincipal.getClientId();
        log.info("Deleting brand {} for client {}", brandId, clientId);
        
        try {
            brandService.deleteBrand(brandId, clientId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Brand deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (BrandNotFoundException e) {
            log.warn("Brand delete failed: {}", e.getMessage());
            throw e; // Will be handled by @ExceptionHandler
        }
    }
}
package com.elowen.identity.controller;

import com.elowen.identity.dto.CreateUserRequest;
import com.elowen.identity.dto.UpdateUserRequest;
import com.elowen.identity.dto.UserResponse;
import com.elowen.identity.security.UserPrincipal;
import com.elowen.identity.service.UserManagementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Admin Panel User Management operations
 * All endpoints require ADMIN role and are tenant-safe
 * All operations are tenant-safe using authenticated UserPrincipal
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /**
     * Create a new user - ADMIN only
     * POST /api/admin/users
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("CREATE USER request from admin: {} for client: {}", 
            principal.getUsername(), principal.getClientId());

        UserResponse userResponse = userManagementService.createUser(request, principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User created successfully");
        response.put("user", userResponse);

        logger.info("User created successfully: {} by admin: {}", 
            userResponse.getUsername(), principal.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all users for admin's client - ADMIN only
     * GET /api/admin/users
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("GET ALL USERS request from user: {} (role: {}) for client: {}", 
            principal.getUsername(), principal.getRole(), principal.getClientId());

        List<UserResponse> users = userManagementService.getAllUsers(principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Users retrieved successfully");
        response.put("users", users);
        response.put("count", users.size());

        logger.info("Retrieved {} users for admin: {}", users.size(), principal.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Get user by ID - ADMIN only
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("GET USER request for userId: {} from user: {} (role: {}) for client: {}", 
            userId, principal.getUsername(), principal.getRole(), principal.getClientId());

        UserResponse userResponse = userManagementService.getUserById(userId, principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User retrieved successfully");
        response.put("user", userResponse);

        logger.info("User retrieved successfully: {} by admin: {}", 
            userResponse.getUsername(), principal.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Update user - ADMIN ONLY
     * PUT /api/users/{userId}
     */
    @PutMapping("/{userId}")

    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("UPDATE USER request for userId: {} from ADMIN: {} for client: {}", 
            userId, principal.getUsername(), principal.getClientId());

        UserResponse userResponse = userManagementService.updateUser(userId, request, principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User updated successfully");
        response.put("user", userResponse);

        logger.info("User updated successfully: {} by admin: {}", 
            userResponse.getUsername(), principal.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Enable user - ADMIN ONLY
     * PATCH /api/users/{userId}/enable
     */
    @PatchMapping("/{userId}/enable")

    public ResponseEntity<Map<String, Object>> enableUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("ENABLE USER request for userId: {} from ADMIN: {} for client: {}", 
            userId, principal.getUsername(), principal.getClientId());

        UserResponse userResponse = userManagementService.enableUser(userId, principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User enabled successfully");
        response.put("user", userResponse);

        logger.info("User enabled successfully: {} by admin: {}", 
            userResponse.getUsername(), principal.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Disable user (soft delete) - ADMIN ONLY
     * PATCH /api/users/{userId}/disable
     */
    @PatchMapping("/{userId}/disable")

    public ResponseEntity<Map<String, Object>> disableUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("DISABLE USER request for userId: {} from ADMIN: {} for client: {}", 
            userId, principal.getUsername(), principal.getClientId());

        UserResponse userResponse = userManagementService.disableUser(userId, principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User disabled successfully");
        response.put("user", userResponse);

        logger.info("User disabled successfully: {} by admin: {}", 
            userResponse.getUsername(), principal.getUsername());

        return ResponseEntity.ok(response);
    }

    /**
     * Delete user (permanent delete) - ADMIN ONLY
     * DELETE /api/admin/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        logger.info("DELETE USER request for userId: {} from ADMIN: {} for client: {}", 
            userId, principal.getUsername(), principal.getClientId());

        userManagementService.deleteUser(userId, principal);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deleted successfully");

        logger.info("User deleted successfully: {} by admin: {}", 
            userId, principal.getUsername());

        return ResponseEntity.ok(response);
    }
}

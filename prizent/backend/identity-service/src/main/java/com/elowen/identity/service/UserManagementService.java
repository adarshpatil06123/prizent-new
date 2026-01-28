package com.elowen.identity.service;

import com.elowen.identity.dto.CreateUserRequest;
import com.elowen.identity.dto.UpdateUserRequest;
import com.elowen.identity.dto.UserResponse;
import com.elowen.identity.entity.Client;
import com.elowen.identity.entity.User;
import com.elowen.identity.exception.ResourceNotFoundException;
import com.elowen.identity.exception.UnauthorizedOperationException;
import com.elowen.identity.exception.UserAlreadyExistsException;
import com.elowen.identity.exception.UserLimitExceededException;
import com.elowen.identity.repository.ClientRepository;
import com.elowen.identity.repository.UserRepository;
import com.elowen.identity.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for User Management operations - ADMIN only
 * All operations are tenant-safe and extract client_id from authenticated UserPrincipal
 */
@Service
@Transactional
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementService(UserRepository userRepository, 
                               ClientRepository clientRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user - ADMIN only
     * Validates user limits and uniqueness constraints within client scope
     */
    public UserResponse createUser(CreateUserRequest request, UserPrincipal principal) {
        // DEFENSIVE CHECK: Ensure only ADMIN can create users
        if (!principal.isAdmin()) {
            logger.error("UNAUTHORIZED: User creation attempt by non-ADMIN user: {} with role: {}", 
                principal.getUsername(), principal.getRole());
            throw new UnauthorizedOperationException("Only ADMIN can create users");
        }

        logger.info("Creating user for client: {}, by ADMIN: {}", 
            principal.getClientId(), principal.getUsername());

        // Extract client_id from authenticated principal (NEVER from request)
        UUID clientId = principal.getClientId();

        // Validate client exists and is enabled
        Client client = validateClientExists(clientId);
        if (!client.getEnabled()) {
            throw new UnauthorizedOperationException("Client is disabled");
        }

        // Check user limits
        long currentUserCount = userRepository.countActiveUsersByClientId(clientId);
        if (currentUserCount >= client.getNumberOfUsersAllowed()) {
            throw new UserLimitExceededException(
                String.format("User limit exceeded. Current: %d, Allowed: %d", 
                    currentUserCount, client.getNumberOfUsersAllowed()));
        }

        // Validate username uniqueness within client
        if (userRepository.existsByUsernameAndClientId(request.getUsername(), clientId)) {
            throw new UserAlreadyExistsException(
                "Username '" + request.getUsername() + "' already exists for this client");
        }

        // Validate email uniqueness within client
        if (userRepository.existsByEmailIdAndClientId(request.getEmailId(), clientId)) {
            throw new UserAlreadyExistsException(
                "Email '" + request.getEmailId() + "' already exists for this client");
        }

        // Create and save user
        User user = new User(
            clientId,
            request.getName(),
            request.getUsername(),
            request.getEmailId(),
            request.getPhoneNumber(),
            request.getEmployeeDesignation(),
            request.getRole(),
            passwordEncoder.encode(request.getPassword()), // BCrypt encryption
            request.getEnabled() != null ? request.getEnabled() : true // Use frontend choice or default to enabled
        );

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {} for client: {}", 
            savedUser.getUsername(), clientId);

        return UserResponse.from(savedUser);
    }

    /**
     * Update existing user - ADMIN only
     * Only allows updating specific fields, maintains client_id security
     */
    public UserResponse updateUser(UUID userId, UpdateUserRequest request, UserPrincipal principal) {
        // DEFENSIVE CHECK: Ensure only ADMIN can update users
        if (!principal.isAdmin()) {
            logger.error("UNAUTHORIZED: User update attempt by non-ADMIN user: {} with role: {}", 
                principal.getUsername(), principal.getRole());
            throw new UnauthorizedOperationException("Only ADMIN can update users");
        }

        logger.info("Updating user: {} for client: {}, by ADMIN: {}", 
            userId, principal.getClientId(), principal.getUsername());

        UUID clientId = principal.getClientId();

        // Find user within admin's client (tenant-safe)
        User user = userRepository.findByIdAndClientId(userId, clientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found or does not belong to your organization"));

        // Update only allowed fields
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmployeeDesignation(request.getEmployeeDesignation());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getUsername());

        return UserResponse.from(updatedUser);
    }

    /**
     * Enable user - ADMIN only
     * Checks client limits before enabling
     */
    public UserResponse enableUser(UUID userId, UserPrincipal principal) {
        // DEFENSIVE CHECK: Ensure only ADMIN can enable users
        if (!principal.isAdmin()) {
            logger.error("UNAUTHORIZED: User enable attempt by non-ADMIN user: {} with role: {}", 
                principal.getUsername(), principal.getRole());
            throw new UnauthorizedOperationException("Only ADMIN can enable users");
        }

        logger.info("Enabling user: {} for client: {}, by ADMIN: {}", 
            userId, principal.getClientId(), principal.getUsername());

        UUID clientId = principal.getClientId();

        // Validate client is enabled
        Client client = validateClientExists(clientId);
        if (!client.getEnabled()) {
            throw new UnauthorizedOperationException("Cannot enable users: Client is disabled");
        }

        // Find user within admin's client (tenant-safe)
        User user = userRepository.findByIdAndClientId(userId, clientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found or does not belong to your organization"));

        // Check if user is already enabled
        if (user.getEnabled()) {
            logger.info("User {} is already enabled", user.getUsername());
            return UserResponse.from(user);
        }

        // Check user limits before enabling
        long currentActiveUsers = userRepository.countActiveUsersByClientId(clientId);
        if (currentActiveUsers >= client.getNumberOfUsersAllowed()) {
            throw new UserLimitExceededException(
                String.format("Cannot enable user: User limit exceeded. Active: %d, Allowed: %d", 
                    currentActiveUsers, client.getNumberOfUsersAllowed()));
        }

        // Enable user
        user.setEnabled(true);
        User enabledUser = userRepository.save(user);
        
        logger.info("User enabled successfully: {}", enabledUser.getUsername());
        return UserResponse.from(enabledUser);
    }

    /**
     * Disable user (soft delete) - ADMIN only
     * ADMIN cannot disable their own account
     */
    public UserResponse disableUser(UUID userId, UserPrincipal principal) {
        // DEFENSIVE CHECK: Ensure only ADMIN can disable users
        if (!principal.isAdmin()) {
            logger.error("UNAUTHORIZED: User disable attempt by non-ADMIN user: {} with role: {}", 
                principal.getUsername(), principal.getRole());
            throw new UnauthorizedOperationException("Only ADMIN can disable users");
        }

        logger.info("Disabling user: {} for client: {}, by ADMIN: {}", 
            userId, principal.getClientId(), principal.getUsername());

        UUID clientId = principal.getClientId();

        // Find user within admin's client (tenant-safe)
        User user = userRepository.findByIdAndClientId(userId, clientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found or does not belong to your organization"));

        // Prevent ADMIN from disabling their own account
        if (user.getId().equals(principal.getUserId())) {
            throw new UnauthorizedOperationException("Cannot disable your own account");
        }

        // Check if user is already disabled
        if (!user.getEnabled()) {
            logger.info("User {} is already disabled", user.getUsername());
            return UserResponse.from(user);
        }

        // Disable user (soft delete)
        user.setEnabled(false);
        User disabledUser = userRepository.save(user);
        
        logger.info("User disabled successfully: {}", disabledUser.getUsername());
        return UserResponse.from(disabledUser);
    }

    /**
     * Get all users for admin's client - ADMIN only
     * Returns client-scoped users only
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(UserPrincipal principal) {
        logger.info("Fetching all users for client: {}", principal.getClientId());

        UUID clientId = principal.getClientId();
        List<User> users = userRepository.findAllByClientId(clientId);

        List<UserResponse> userResponses = users.stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());

        logger.info("Found {} users for client: {}", userResponses.size(), clientId);
        return userResponses;
    }

    /**
     * Get user by ID - ADMIN only
     * Returns user only if it belongs to admin's client
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId, UserPrincipal principal) {
        logger.info("Fetching user: {} for client: {}", userId, principal.getClientId());

        UUID clientId = principal.getClientId();
        
        User user = userRepository.findByIdAndClientId(userId, clientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found or does not belong to your organization"));

        logger.info("Found user: {}", user.getUsername());
        return UserResponse.from(user);
    }

    /**
     * Delete user (permanent delete) - ADMIN only
     * ADMIN cannot delete their own account
     */
    public void deleteUser(UUID userId, UserPrincipal principal) {
        // DEFENSIVE CHECK: Ensure only ADMIN can delete users
        if (!principal.isAdmin()) {
            logger.error("UNAUTHORIZED: User delete attempt by non-ADMIN user: {} with role: {}", 
                principal.getUsername(), principal.getRole());
            throw new UnauthorizedOperationException("Only ADMIN can delete users");
        }

        logger.info("Deleting user: {} for client: {}, by ADMIN: {}", 
            userId, principal.getClientId(), principal.getUsername());

        UUID clientId = principal.getClientId();

        // Find user within admin's client (tenant-safe)
        User user = userRepository.findByIdAndClientId(userId, clientId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found or does not belong to your organization"));

        // Prevent ADMIN from deleting their own account
        if (user.getId().equals(principal.getUserId())) {
            throw new UnauthorizedOperationException("Cannot delete your own account");
        }

        // Permanent delete
        userRepository.delete(user);

        logger.info("User deleted permanently: {} by admin: {}", user.getUsername(), principal.getUsername());
    }

    /**
     * Helper method to validate client exists
     */
    private Client validateClientExists(UUID clientId) {
        return clientRepository.findById(clientId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }
}

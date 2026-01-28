package com.elowen.admin.security;

import java.util.UUID;

/**
 * Principal class to hold authenticated user details extracted from JWT
 * This should match the UserPrincipal from identity-service
 */
public class UserPrincipal {
    
    private final UUID userId;
    private final UUID clientId;
    private final String username;
    private final String role;

    public UserPrincipal(UUID userId, UUID clientId, String username, String role) {
        this.userId = userId;
        this.clientId = clientId;
        this.username = username;
        this.role = role;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
    
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "userId=" + userId +
                ", clientId=" + clientId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
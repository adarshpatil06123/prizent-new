package com.elowen.identity.dto;

import com.elowen.identity.entity.Role;
import com.elowen.identity.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user response - excludes sensitive information like passwords
 */
public class UserResponse {

    private UUID id;
    private UUID clientId;
    private String name;
    private String username;
    private String emailId;
    private String phoneNumber;
    private String employeeDesignation;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    // Constructors
    public UserResponse() {}

    public UserResponse(UUID id, UUID clientId, String name, String username, 
                       String emailId, String phoneNumber, String employeeDesignation, 
                       Role role, Boolean enabled, LocalDateTime createDateTime, 
                       LocalDateTime updateDateTime) {
        this.id = id;
        this.clientId = clientId;
        this.name = name;
        this.username = username;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.employeeDesignation = employeeDesignation;
        this.role = role;
        this.enabled = enabled;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

    /**
     * Factory method to create UserResponse from User entity
     */
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getClientId(),
            user.getName(),
            user.getUsername(),
            user.getEmailId(),
            user.getPhoneNumber(),
            user.getEmployeeDesignation(),
            user.getRole(),
            user.getEnabled(),
            user.getCreateDateTime(),
            user.getUpdateDateTime()
        );
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

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmployeeDesignation() {
        return employeeDesignation;
    }

    public void setEmployeeDesignation(String employeeDesignation) {
        this.employeeDesignation = employeeDesignation;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        return "UserResponse{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", emailId='" + emailId + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
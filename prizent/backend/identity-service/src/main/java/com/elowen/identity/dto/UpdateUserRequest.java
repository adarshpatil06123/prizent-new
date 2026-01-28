package com.elowen.identity.dto;

import com.elowen.identity.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating existing user - ADMIN only operation
 * Note: Does NOT include username, client_id, password, enabled, or timestamps
 */
public class UpdateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 100, message = "Employee designation must not exceed 100 characters")
    private String employeeDesignation;

    @NotNull(message = "Role is required")
    private Role role;

    // Constructors
    public UpdateUserRequest() {}

    public UpdateUserRequest(String name, String phoneNumber, 
                           String employeeDesignation, Role role) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.employeeDesignation = employeeDesignation;
        this.role = role;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", employeeDesignation='" + employeeDesignation + '\'' +
                ", role=" + role +
                '}';
    }
}
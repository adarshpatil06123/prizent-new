package com.elowen.identity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"client_id", "username"}, name = "uk_client_username"),
           @UniqueConstraint(columnNames = {"client_id", "email_id"}, name = "uk_client_email")
       })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Client ID is required")
    @Column(name = "client_id", nullable = false, updatable = false) // immutable
    private UUID clientId;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, length = 100, updatable = false) // immutable
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(name = "email_id", nullable = false, length = 255)
    private String emailId;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "employee_designation", length = 100)
    private String employeeDesignation;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false, length = 255)
    private String password; // BCrypt encrypted

    @NotNull(message = "Enabled status is required")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "create_date_time", nullable = false, updatable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "update_date_time", nullable = false)
    private LocalDateTime updateDateTime;

    // Constructors
    public User() {}

    public User(UUID clientId, String name, String username, String emailId, String phoneNumber, 
                String employeeDesignation, Role role, String password, Boolean enabled) {
        this.clientId = clientId;
        this.name = name;
        this.username = username;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.employeeDesignation = employeeDesignation;
        this.role = role;
        this.password = password;
        this.enabled = enabled != null ? enabled : true;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return "User{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
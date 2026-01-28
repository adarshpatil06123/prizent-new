package com.elowen.identity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_login_logout_histories")
public class LoginLogoutHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Client ID is required")
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotBlank(message = "Username is required")
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @CreationTimestamp
    @Column(name = "login_date_time", nullable = false, updatable = false)
    private LocalDateTime loginDateTime;

    @Column(name = "logout_date_time")
    private LocalDateTime logoutDateTime;

    // Constructors
    public LoginLogoutHistory() {}

    public LoginLogoutHistory(UUID clientId, UUID userId, String userName) {
        this.clientId = clientId;
        this.userId = userId;
        this.userName = userName;
        this.loginDateTime = LocalDateTime.now();
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getLoginDateTime() {
        return loginDateTime;
    }

    public void setLoginDateTime(LocalDateTime loginDateTime) {
        this.loginDateTime = loginDateTime;
    }

    public LocalDateTime getLogoutDateTime() {
        return logoutDateTime;
    }

    public void setLogoutDateTime(LocalDateTime logoutDateTime) {
        this.logoutDateTime = logoutDateTime;
    }

    @Override
    public String toString() {
        return "LoginLogoutHistory{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", loginDateTime=" + loginDateTime +
                ", logoutDateTime=" + logoutDateTime +
                '}';
    }
}
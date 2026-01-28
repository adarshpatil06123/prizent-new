package com.elowen.identity.controller;

import com.elowen.identity.entity.User;
import com.elowen.identity.repository.UserRepository;
import com.elowen.identity.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String userId = jwtUtil.extractUserId(token);
            Optional<User> userOpt = userRepository.findById(UUID.fromString(userId));

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = userOpt.get();

            // Create a response object without sensitive information
            UserProfileResponse profile = new UserProfileResponse();
            profile.setId(user.getId());
            profile.setUsername(user.getUsername());
            profile.setName(user.getName());
            profile.setEmailId(user.getEmailId());
            profile.setPhoneNumber(user.getPhoneNumber());
            profile.setEmployeeDesignation(user.getEmployeeDesignation());
            profile.setRole(user.getRole().toString());
            profile.setEnabled(user.getEnabled());
            profile.setCreateDateTime(user.getCreateDateTime());
            profile.setUpdateDateTime(user.getUpdateDateTime());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // Inner class for the response
    public static class UserProfileResponse {
        private UUID id;
        private String username;
        private String name;
        private String emailId;
        private String phoneNumber;
        private String employeeDesignation;
        private String role;
        private Boolean enabled;
        private java.time.LocalDateTime createDateTime;
        private java.time.LocalDateTime updateDateTime;

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmailId() { return emailId; }
        public void setEmailId(String emailId) { this.emailId = emailId; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getEmployeeDesignation() { return employeeDesignation; }
        public void setEmployeeDesignation(String employeeDesignation) { this.employeeDesignation = employeeDesignation; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }

        public java.time.LocalDateTime getCreateDateTime() { return createDateTime; }
        public void setCreateDateTime(java.time.LocalDateTime createDateTime) { this.createDateTime = createDateTime; }

        public java.time.LocalDateTime getUpdateDateTime() { return updateDateTime; }
        public void setUpdateDateTime(java.time.LocalDateTime updateDateTime) { this.updateDateTime = updateDateTime; }
    }
}
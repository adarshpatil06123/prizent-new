package com.elowen.identity.controller;

import com.elowen.identity.dto.LoginRequest;
import com.elowen.identity.dto.LoginResponse;
import com.elowen.identity.entity.User;
import com.elowen.identity.entity.Client;
import com.elowen.identity.entity.LoginLogoutHistory;
import com.elowen.identity.repository.UserRepository;
import com.elowen.identity.repository.ClientRepository;
import com.elowen.identity.repository.LoginLogoutHistoryRepository;
import com.elowen.identity.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoginLogoutHistoryRepository loginLogoutHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // For simplicity, always use Test Client (in production, you'd validate clientId properly)
            Optional<Client> clientOpt = clientRepository.findByName("Test Client");
            if (clientOpt.isEmpty()) {
                return ResponseEntity.status(401).body(new LoginResponse(false, "Client not found", null));
            }

            Client client = clientOpt.get();

            // Find user by username OR email and client
            Optional<User> userOpt = userRepository.findByUsernameOrEmailAndClientId(loginRequest.getUsername(), client.getId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).body(new LoginResponse(false, "Invalid credentials", null));
            }

            User user = userOpt.get();

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body(new LoginResponse(false, "Invalid credentials", null));
            }

            // Check if user has admin role - only ADMIN users can access this admin panel
            if (!user.getRole().toString().equals("ADMIN")) {
                return ResponseEntity.status(403).body(new LoginResponse(false, "Access denied: Admin privileges required", null));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(
                client.getId().toString(), // Use client UUID instead of name
                user.getId().toString(),
                user.getRole().toString()
            );

            // Log successful login
            LoginLogoutHistory loginHistory = new LoginLogoutHistory();
            loginHistory.setClientId(client.getId());
            loginHistory.setUserId(user.getId());
            loginHistory.setUserName(user.getUsername());
            loginLogoutHistoryRepository.save(loginHistory);

            return ResponseEntity.ok(new LoginResponse(true, "Login successful", token));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new LoginResponse(false, "Internal server error", null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            return ResponseEntity.ok("Logout successful");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
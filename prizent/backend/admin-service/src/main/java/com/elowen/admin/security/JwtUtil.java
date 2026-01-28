package com.elowen.admin.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey secretKey;

    // JWT claim keys
    public static final String CLAIM_CLIENT_ID = "client_id";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_ROLE = "role";

    public JwtUtil(@Value("${jwt.secret:mySecretKeyForDevelopmentOnlyChangeInProduction}") String secret) {
        // Ensure the secret is strong enough (minimum 32 bytes for HS256)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes for HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract all claims from JWT token
     */
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extract username from JWT token (subject)
     */
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * Extract client ID from JWT token
     */
    public UUID getClientIdFromToken(String token) {
        String clientId = getAllClaimsFromToken(token).get(CLAIM_CLIENT_ID, String.class);
        return clientId != null ? UUID.fromString(clientId) : null;
    }

    /**
     * Extract user ID from JWT token
     */
    public UUID getUserIdFromToken(String token) {
        String userId = getAllClaimsFromToken(token).get(CLAIM_USER_ID, String.class);
        return userId != null ? UUID.fromString(userId) : null;
    }

    /**
     * Extract role from JWT token
     */
    public String getRoleFromToken(String token) {
        return getAllClaimsFromToken(token).get(CLAIM_ROLE, String.class);
    }

    /**
     * Check if JWT token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getAllClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Validate JWT token
     */
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract JWT token from Authorization header
     */
    public String getTokenFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
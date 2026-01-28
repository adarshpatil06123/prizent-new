package com.elowen.identity.security;

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
    private final long jwtExpirationMs;

    // JWT claim keys
    public static final String CLAIM_CLIENT_ID = "client_id";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_ROLE = "role";

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long jwtExpirationMs) {
        // Ensure the secret is strong enough (minimum 32 bytes for HS256)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes for HS256");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generate JWT token with client_id, user_id, and role
     */
    public String generateToken(String clientId, String userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userId) // Use userId as subject
                .claim(CLAIM_CLIENT_ID, clientId)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !isTokenExpired(claims);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract client ID from token
     */
    public String extractClientId(String token) {
        return getClaims(token).get(CLAIM_CLIENT_ID, String.class);
    }

    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        return getClaims(token).get(CLAIM_USER_ID, String.class);
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return getClaims(token).get(CLAIM_ROLE, String.class);
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract client ID from token
     */
    public UUID getClientIdFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            String clientIdStr = claims.get(CLAIM_CLIENT_ID, String.class);
            return clientIdStr != null ? UUID.fromString(clientIdStr) : null;
        } catch (Exception e) {
            logger.error("Error extracting client ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract user ID from token
     */
    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            String userIdStr = claims.get(CLAIM_USER_ID, String.class);
            return userIdStr != null ? UUID.fromString(userIdStr) : null;
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get(CLAIM_ROLE, String.class);
        } catch (Exception e) {
            logger.error("Error extracting role from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Error extracting expiration date from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validate token - checks signature, expiration, and required claims
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            
            // Check if token has expired
            if (isTokenExpired(claims)) {
                logger.warn("Token has expired");
                return false;
            }

            // Check if required claims are present
            if (claims.get(CLAIM_CLIENT_ID) == null || 
                claims.get(CLAIM_USER_ID) == null || 
                claims.get(CLAIM_ROLE) == null) {
                logger.warn("Token missing required claims");
                return false;
            }

            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT token validation error: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extract all claims from token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * Get token expiration time in milliseconds
     */
    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}
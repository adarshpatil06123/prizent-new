package com.elowen.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            
            try {
                if (jwtUtil.isTokenValid(jwt)) {
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    UUID userId = jwtUtil.getUserIdFromToken(jwt);
                    UUID clientId = jwtUtil.getClientIdFromToken(jwt);
                    String role = jwtUtil.getRoleFromToken(jwt);
                    
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Create UserPrincipal with extracted info
                        UserPrincipal userPrincipal = new UserPrincipal(userId, clientId, username, role);
                        
                        // Create authority from role
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        
                        // Create authentication with role-based authority
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userPrincipal, null, Collections.singletonList(authority));
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        logger.debug("Set authentication for user: {} (client: {}, role: {})", userId, clientId, role);
                    }
                }
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
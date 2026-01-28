package com.elowen.identity.repository;

import com.elowen.identity.entity.Client;
import com.elowen.identity.entity.Role;
import com.elowen.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username within a specific client (for authentication)
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.clientId = :clientId")
    Optional<User> findByUsernameAndClientId(@Param("username") String username, @Param("clientId") UUID clientId);

    /**
     * Find user by username OR email within a specific client (for flexible login)
     */
    @Query("SELECT u FROM User u WHERE (u.username = :usernameOrEmail OR u.emailId = :usernameOrEmail) AND u.clientId = :clientId")
    Optional<User> findByUsernameOrEmailAndClientId(@Param("usernameOrEmail") String usernameOrEmail, @Param("clientId") UUID clientId);

    /**
     * Find user by ID within a specific client (tenant-safe)
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.clientId = :clientId")
    Optional<User> findByIdAndClientId(@Param("id") UUID id, @Param("clientId") UUID clientId);

    /**
     * Find enabled user by ID within a specific client
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.clientId = :clientId AND u.enabled = true")
    Optional<User> findEnabledByIdAndClientId(@Param("id") UUID id, @Param("clientId") UUID clientId);

    /**
     * Find enabled user for login - checks both user and client enabled status
     * This is the LOGIN-SAFE method that verifies all security constraints
     */
    @Query("SELECT u FROM User u JOIN Client c ON u.clientId = c.id " +
           "WHERE u.username = :username AND u.clientId = :clientId " +
           "AND u.enabled = true AND c.enabled = true")
    Optional<User> findEnabledUserForLogin(@Param("username") String username, @Param("clientId") UUID clientId);

    /**
     * Find all users for a specific client (tenant-safe)
     */
    @Query("SELECT u FROM User u WHERE u.clientId = :clientId ORDER BY u.createDateTime DESC")
    List<User> findAllByClientId(@Param("clientId") UUID clientId);

    /**
     * Find all enabled users for a specific client
     */
    @Query("SELECT u FROM User u WHERE u.clientId = :clientId AND u.enabled = true ORDER BY u.createDateTime DESC")
    List<User> findAllEnabledByClientId(@Param("clientId") UUID clientId);

    /**
     * Find all users with specific role for a client
     */
    @Query("SELECT u FROM User u WHERE u.clientId = :clientId AND u.role = :role ORDER BY u.createDateTime DESC")
    List<User> findByClientIdAndRole(@Param("clientId") UUID clientId, @Param("role") Role role);

    /**
     * Check if username exists within a client (for validation)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.clientId = :clientId")
    boolean existsByUsernameAndClientId(@Param("username") String username, @Param("clientId") UUID clientId);

    /**
     * Check if email exists within a client (for validation)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.emailId = :emailId AND u.clientId = :clientId")
    boolean existsByEmailIdAndClientId(@Param("emailId") String emailId, @Param("clientId") UUID clientId);

    /**
     * Check if username exists within a client, excluding specific user (for updates)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.clientId = :clientId AND u.id != :excludeUserId")
    boolean existsByUsernameAndClientIdExcludingUser(@Param("username") String username, 
                                                     @Param("clientId") UUID clientId, 
                                                     @Param("excludeUserId") UUID excludeUserId);

    /**
     * Check if email exists within a client, excluding specific user (for updates)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.emailId = :emailId AND u.clientId = :clientId AND u.id != :excludeUserId")
    boolean existsByEmailIdAndClientIdExcludingUser(@Param("emailId") String emailId, 
                                                    @Param("clientId") UUID clientId, 
                                                    @Param("excludeUserId") UUID excludeUserId);

    /**
     * Count active users for a client (for user limit validation)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.clientId = :clientId AND u.enabled = true")
    long countActiveUsersByClientId(@Param("clientId") UUID clientId);

    /**
     * Find user by email within a specific client
     */
    @Query("SELECT u FROM User u WHERE u.emailId = :emailId AND u.clientId = :clientId")
    Optional<User> findByEmailIdAndClientId(@Param("emailId") String emailId, @Param("clientId") UUID clientId);
}
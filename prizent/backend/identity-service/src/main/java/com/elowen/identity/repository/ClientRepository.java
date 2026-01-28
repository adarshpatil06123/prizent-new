package com.elowen.identity.repository;

import com.elowen.identity.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Find client by ID only if enabled
     */
    Optional<Client> findByIdAndEnabled(UUID id, Boolean enabled);

    /**
     * Find enabled client by ID
     */
    default Optional<Client> findEnabledById(UUID id) {
        return findByIdAndEnabled(id, true);
    }

    /**
     * Check if client exists and is enabled
     */
    boolean existsByIdAndEnabled(UUID id, Boolean enabled);

    /**
     * Check if enabled client exists
     */
    default boolean existsEnabledById(UUID id) {
        return existsByIdAndEnabled(id, true);
    }

    /**
     * Find client by name
     */
    Optional<Client> findByName(String name);

    /**
     * Count total users for a client (used for user limit validation)
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.clientId = :clientId AND u.enabled = true")
    long countActiveUsersByClientId(@Param("clientId") UUID clientId);
}
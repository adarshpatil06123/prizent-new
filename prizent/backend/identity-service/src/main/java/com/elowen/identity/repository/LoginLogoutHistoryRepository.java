package com.elowen.identity.repository;

import com.elowen.identity.entity.LoginLogoutHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginLogoutHistoryRepository extends JpaRepository<LoginLogoutHistory, UUID> {

    /**
     * Find all login histories for a specific client (tenant-safe)
     */
    @Query("SELECT h FROM LoginLogoutHistory h WHERE h.clientId = :clientId ORDER BY h.loginDateTime DESC")
    List<LoginLogoutHistory> findAllByClientId(@Param("clientId") UUID clientId);

    /**
     * Find all login histories for a specific user within a client
     */
    @Query("SELECT h FROM LoginLogoutHistory h WHERE h.clientId = :clientId AND h.userId = :userId ORDER BY h.loginDateTime DESC")
    List<LoginLogoutHistory> findByClientIdAndUserId(@Param("clientId") UUID clientId, @Param("userId") UUID userId);

    /**
     * Find the latest login record for a user (used for logout)
     */
    @Query("SELECT h FROM LoginLogoutHistory h WHERE h.clientId = :clientId AND h.userId = :userId " +
           "AND h.logoutDateTime IS NULL ORDER BY h.loginDateTime DESC LIMIT 1")
    Optional<LoginLogoutHistory> findLatestActiveLoginByClientIdAndUserId(@Param("clientId") UUID clientId, @Param("userId") UUID userId);

    /**
     * Update logout time for a specific login record
     */
    @Modifying
    @Query("UPDATE LoginLogoutHistory h SET h.logoutDateTime = :logoutDateTime WHERE h.id = :id")
    void updateLogoutTime(@Param("id") UUID id, @Param("logoutDateTime") LocalDateTime logoutDateTime);

    /**
     * Find all active sessions (no logout time) for a client
     */
    @Query("SELECT h FROM LoginLogoutHistory h WHERE h.clientId = :clientId AND h.logoutDateTime IS NULL ORDER BY h.loginDateTime DESC")
    List<LoginLogoutHistory> findActiveSessionsByClientId(@Param("clientId") UUID clientId);

    /**
     * Count total logins for a user within a client
     */
    @Query("SELECT COUNT(h) FROM LoginLogoutHistory h WHERE h.clientId = :clientId AND h.userId = :userId")
    long countLoginsByClientIdAndUserId(@Param("clientId") UUID clientId, @Param("userId") UUID userId);

    /**
     * Find login histories within a date range for a client
     */
    @Query("SELECT h FROM LoginLogoutHistory h WHERE h.clientId = :clientId " +
           "AND h.loginDateTime >= :startDate AND h.loginDateTime <= :endDate " +
           "ORDER BY h.loginDateTime DESC")
    List<LoginLogoutHistory> findByClientIdAndDateRange(@Param("clientId") UUID clientId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
}
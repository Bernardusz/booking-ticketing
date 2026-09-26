package io.github.bernardusz.booking_ticketing.auth

import org.springframework.transaction.annotation.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface AuthRepository : JpaRepository<RefreshToken, Long> {
    // Returns all tokens for a given user (e.g., across multiple active devices)
    fun findAllByUserId(userId: Long): List<RefreshToken>
    // Returns all active, unrevoked tokens for a given user
    fun findAllByUserIdAndRevokedFalse(userId: Long): List<RefreshToken>
    // Returns a single active token matching the SHA-256 hash
    fun findByTokenHash(tokenHash: String): Optional<RefreshToken>

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false")
    fun revokeAllByUserId(userId: Long): Int

    // Delete expired tokens to keep the table size small (Scheduled cleanup job)
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < CURRENT_TIMESTAMP OR r.revoked = true")
    fun deleteAllExpiredOrRevoked(): Int
}
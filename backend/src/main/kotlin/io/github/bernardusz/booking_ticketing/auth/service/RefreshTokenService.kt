package io.github.bernardusz.booking_ticketing.auth.service

import io.github.bernardusz.booking_ticketing.auth.AuthRepository
import io.github.bernardusz.booking_ticketing.auth.RefreshToken
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.external.RefreshTokenException
import io.github.bernardusz.booking_ticketing.user.User
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.HexFormat
import java.util.UUID

@Service
class RefreshTokenService (
    private val authRepository: AuthRepository,
    private val entityManager: EntityManager,
    @Value("\${jwt.refresh_token.expiration}") private val refreshTokenExpirationMs: Long
) {
    fun hashToken(rawToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawToken.toByteArray())
        return HexFormat.of().formatHex(hashBytes)
    }

    @Transactional
    fun createRefreshToken(userId: Long): String {
        val rawUuid = UUID.randomUUID().toString()
        val hashedToken: String = hashToken(rawUuid)

        val userRef = entityManager.getReference(User::class.java, userId)

        val refreshToken = RefreshToken(
            user = userRef,
            tokenHash = hashedToken,
            expiresAt = Instant.now().plusMillis(refreshTokenExpirationMs),
            revoked = false
        )

        authRepository.save(refreshToken)
        return rawUuid
    }

    @Transactional(readOnly = true)
    fun findValidToken(rawToken: String): RefreshToken {
        val hashedToken = hashToken(rawToken)
        val token: RefreshToken = authRepository.findByTokenHash(hashedToken)
            .orElseThrow {
                throw RefreshTokenException("No refresh token found", 404)
            }

        if (token.revoked){
            throw RefreshTokenException("Access token had been revoked", 401)
        }

        if (token.expiresAt.isBefore(Instant.now())){
            throw RefreshTokenException("Access token has expired", 401)
        }

        return token
    }

    @Transactional(readOnly = true)
    fun getUserTokens(userId: Long): List<RefreshToken> {
        return authRepository.findAllByUserId(userId)
    }

    @Transactional
    fun revokeToken(rawToken: String) {
        val hashedToken = hashToken(rawToken)
        authRepository.findByTokenHash(hashedToken).ifPresent {
            token ->
                token.revoked = true
                authRepository.save(token)
        }
    }

    @Transactional
    fun revokeAllUserSessions(userId: Long): Int {
        return authRepository.revokeAllByUserId(userId)
    }

    @Transactional
    fun purgeExpiredTokensOrRevokedTokens(): Int {
        return authRepository.deleteAllExpiredOrRevoked()
    }

    fun getRefreshTokenExpiration(): Long {
        return refreshTokenExpirationMs
    }
}
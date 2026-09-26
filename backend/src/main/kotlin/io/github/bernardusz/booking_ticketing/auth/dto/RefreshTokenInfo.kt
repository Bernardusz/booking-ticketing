package io.github.bernardusz.booking_ticketing.auth.dto

import java.time.Instant

data class RefreshTokenInfo(
    val id: Long,
    val userId: Long,
    val tokenHash: String,
    val salt: String,
    val expiresAt: Instant
)

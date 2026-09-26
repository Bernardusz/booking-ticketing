package io.github.bernardusz.booking_ticketing.auth.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val refreshTokenExpiresIn: Long
)
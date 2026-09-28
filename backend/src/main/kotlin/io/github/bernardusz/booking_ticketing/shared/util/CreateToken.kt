package io.github.bernardusz.booking_ticketing.shared.util

import org.springframework.http.ResponseCookie

internal fun createAccessTokenCookie(token: String, maxAgeSeconds: Long): ResponseCookie =
    ResponseCookie.from("AUTH-TOKEN", token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(maxAgeSeconds)
        .sameSite("Lax")
        .build()

internal fun createRefreshTokenCookie(token: String, maxAgeSeconds: Long): ResponseCookie =
    ResponseCookie.from("REFRESH-TOKEN", token)
        .httpOnly(true)
        .secure(true)
        .path("/api/v1/auth")
        .maxAge(maxAgeSeconds)
        .sameSite("Lax")
        .build()
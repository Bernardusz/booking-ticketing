package io.github.bernardusz.booking_ticketing.shared.exception.dto

import java.time.LocalDateTime

data class ErrorResponse(
    val message: String,
    val code: Int,
    val timestamp: LocalDateTime
)

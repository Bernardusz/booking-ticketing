package io.github.bernardusz.booking_ticketing.auth.dto

import io.github.bernardusz.booking_ticketing.user.Role

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
)

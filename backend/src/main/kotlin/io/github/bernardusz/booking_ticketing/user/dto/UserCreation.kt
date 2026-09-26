package io.github.bernardusz.booking_ticketing.user.dto

data class UserCreation(
    val username: String,
    val password: String,
    val email: String,
)

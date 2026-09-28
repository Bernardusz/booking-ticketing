package io.github.bernardusz.booking_ticketing.user.dto

import io.github.bernardusz.booking_ticketing.user.Role

data class UserInformation(
    val id: Long,
    val username: String,
    val email: String,
    val role: Role = Role.ROLE_USER,
)

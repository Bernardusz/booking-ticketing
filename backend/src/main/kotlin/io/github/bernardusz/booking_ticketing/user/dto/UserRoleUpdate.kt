package io.github.bernardusz.booking_ticketing.user.dto

import io.github.bernardusz.booking_ticketing.user.Role

data class UserRoleUpdate(
    val role: Role = Role.ROLE_USER,
)

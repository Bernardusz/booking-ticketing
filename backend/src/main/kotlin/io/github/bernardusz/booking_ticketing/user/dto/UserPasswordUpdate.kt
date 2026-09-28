package io.github.bernardusz.booking_ticketing.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserPasswordUpdate(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "New password must be at least 8 characters long")
    val password: String,
)

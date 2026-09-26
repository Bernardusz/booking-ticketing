package io.github.bernardusz.booking_ticketing.auth.dto

import io.github.bernardusz.booking_ticketing.user.dto.UserInformation

data class UserSessionResponse(
    val isAuthenticated: Boolean,
    val user: UserInformation? = null
)

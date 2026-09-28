package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.external

import org.springframework.http.HttpStatus

class RefreshTokenException(
    message : String,
    code: Int = HttpStatus.UNAUTHORIZED.value())
    : ExternalUserException(message, code)
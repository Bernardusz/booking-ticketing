package io.github.bernardusz.booking_ticketing.shared.exception.exceptions

import org.springframework.http.HttpStatus

class RefreshTokenException(message : String, val code: Int = HttpStatus.UNAUTHORIZED.value()) : RuntimeException(message)
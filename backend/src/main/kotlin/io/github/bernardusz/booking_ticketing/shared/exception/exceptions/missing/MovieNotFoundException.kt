package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing

import org.springframework.http.HttpStatus

class MovieNotFoundException(
    message: String,
    code: Int = HttpStatus.NOT_FOUND.value(),
) : NotFoundException(message, code)
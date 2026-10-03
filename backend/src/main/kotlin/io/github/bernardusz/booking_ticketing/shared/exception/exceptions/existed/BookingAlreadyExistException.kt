package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed

import org.springframework.http.HttpStatus

class BookingAlreadyExistException(
    message: String,
    code: Int = HttpStatus.CONFLICT.value(),
) : AlreadyExistsException(message, code)
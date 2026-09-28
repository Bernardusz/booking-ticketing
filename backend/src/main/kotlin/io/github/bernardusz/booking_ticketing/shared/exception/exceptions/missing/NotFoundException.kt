package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing

open class NotFoundException(message: String, val code: Int): RuntimeException(message)
package io.github.bernardusz.booking_ticketing.shared.exception.exceptions

class UserAlreadyExistsException(message: String, val code: Int = 409) : RuntimeException(message)
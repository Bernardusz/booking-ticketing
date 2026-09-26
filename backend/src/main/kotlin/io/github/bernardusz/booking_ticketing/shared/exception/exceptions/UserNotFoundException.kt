package io.github.bernardusz.booking_ticketing.shared.exception.exceptions

class UserNotFoundException(message: String, val code: Int = 404): RuntimeException("User not found")
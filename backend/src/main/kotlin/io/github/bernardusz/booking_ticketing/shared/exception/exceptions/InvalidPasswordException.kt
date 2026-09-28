package io.github.bernardusz.booking_ticketing.shared.exception.exceptions

class InvalidPasswordException(message : String, val code: Int = 400) : RuntimeException(message)

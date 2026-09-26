package io.github.bernardusz.booking_ticketing.shared.exception.exceptions

class InternalServerException(message : String, val code: Int = 500) : RuntimeException(message)
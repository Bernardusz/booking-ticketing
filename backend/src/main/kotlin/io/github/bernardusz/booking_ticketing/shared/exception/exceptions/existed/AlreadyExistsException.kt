package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed

open class AlreadyExistsException (message: String, val code: Int) : RuntimeException(message)
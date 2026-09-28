package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.external

open class ExternalUserException(message : String, val code: Int) : RuntimeException(message)
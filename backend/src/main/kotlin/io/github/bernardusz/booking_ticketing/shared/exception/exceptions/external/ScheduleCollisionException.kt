package io.github.bernardusz.booking_ticketing.shared.exception.exceptions.external

import org.springframework.http.HttpStatus

class ScheduleCollisionException(
    message : String,
    code: Int = HttpStatus.BAD_REQUEST.value(),
) : ExternalUserException(message, code)
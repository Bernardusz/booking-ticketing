package io.github.bernardusz.booking_ticketing.tickets.dto

import jakarta.validation.constraints.NotEmpty

data class TicketLockRequest(
    @field:NotEmpty(message = "At least one ticket ID must be provided")
    val ticketIds: List<Long>
)

data class TicketBookingRequest(
    @field:NotEmpty(message = "At least one ticket ID must be provided")
    val ticketIds: List<Long>
)

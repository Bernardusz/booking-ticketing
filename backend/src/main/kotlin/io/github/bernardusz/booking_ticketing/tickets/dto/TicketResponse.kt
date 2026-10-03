package io.github.bernardusz.booking_ticketing.tickets.dto

data class TicketResponse(
    val ticketId: Long,
    val seatId: Long,
    val rowLabel: String,
    val seatNumber: Int,
    val status: String,
    val lockExpiration: String?
)
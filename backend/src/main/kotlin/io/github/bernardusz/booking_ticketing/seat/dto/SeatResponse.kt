package io.github.bernardusz.booking_ticketing.seat.dto

import io.github.bernardusz.booking_ticketing.seat.Seat

data class SeatResponse(
    val id: Long,
    val rowLabel: String,
    val seatNumber: Int,
)

fun Seat.toResponse(): SeatResponse = SeatResponse(
    id = id,
    rowLabel = rowLabel,
    seatNumber = seatNumber
)

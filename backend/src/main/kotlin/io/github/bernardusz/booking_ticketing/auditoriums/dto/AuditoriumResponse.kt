package io.github.bernardusz.booking_ticketing.auditoriums.dto

import io.github.bernardusz.booking_ticketing.auditoriums.Auditorium

data class AuditoriumResponse(
    val id: Long,
    val code: String,
    val name: String,
    val totalSeats: Int
)

fun Auditorium.toResponse() = AuditoriumResponse(
    id = this.id,
    code = this.code,
    name = this.name,
    totalSeats = this.totalSeats,
)
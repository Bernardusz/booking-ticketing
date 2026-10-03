package io.github.bernardusz.booking_ticketing.auditoriums.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AuditoriumSaveRequest(
    @field:NotBlank
    val code: String,

    @field:NotBlank
    val name: String,

    @field:Min(value = 1, message = "Rows count must be at least 1")
    val rowsCount: Int,
    @field:Min(value = 1, message = "Seats per row must be at least 1")
    val seatsPerRow: Int,
){
    val totalSeats: Int get() = rowsCount * seatsPerRow
}
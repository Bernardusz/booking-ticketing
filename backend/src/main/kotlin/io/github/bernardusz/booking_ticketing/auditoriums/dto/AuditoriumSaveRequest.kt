package io.github.bernardusz.booking_ticketing.auditoriums.dto

import io.github.bernardusz.booking_ticketing.auditoriums.Auditorium
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class AuditoriumSaveRequest(
    @field:NotBlank
    val code: String,

    @field:NotBlank
    val name: String,

    @field:Min(value = 1, message = "Amount must be greater than or equal to zero")
    val totalSeats: Int,
)
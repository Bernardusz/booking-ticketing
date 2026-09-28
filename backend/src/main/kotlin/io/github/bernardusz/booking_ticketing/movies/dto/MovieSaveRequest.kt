package io.github.bernardusz.booking_ticketing.movies.dto

import java.time.LocalDate

data class MovieSaveRequest(
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val durationMinutes: Int,
)

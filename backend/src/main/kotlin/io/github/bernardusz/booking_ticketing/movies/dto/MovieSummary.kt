package io.github.bernardusz.booking_ticketing.movies.dto

import io.github.bernardusz.booking_ticketing.movies.Movie
import java.time.LocalDate

data class MovieSummary(
    val id: Long,
    val title: String,
    val releaseDate: LocalDate,
    val durationMinutes: Int,
    val posterUrl: String,
)

fun Movie.toSummary(): MovieSummary = MovieSummary(
    id = this.id,
    title = this.title,
    releaseDate = this.releaseDate,
    durationMinutes = this.durationMinutes,
    posterUrl = this.posterUrl
)

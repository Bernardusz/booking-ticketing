package io.github.bernardusz.booking_ticketing.movies.dto

import io.github.bernardusz.booking_ticketing.movies.Movie
import java.time.LocalDate

data class MovieDetails(
    val id: Long,
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val durationMinutes: Int,
    val posterUrl: String
)

fun Movie.toDetails(): MovieDetails = MovieDetails(
    id = this.id,
    title = this.title,
    description = this.description,
    releaseDate = this.releaseDate,
    durationMinutes = this.durationMinutes,
    posterUrl = this.posterUrl
)

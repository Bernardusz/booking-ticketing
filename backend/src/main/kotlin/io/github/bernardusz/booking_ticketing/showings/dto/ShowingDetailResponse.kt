package io.github.bernardusz.booking_ticketing.showings.dto

import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumResponse
import io.github.bernardusz.booking_ticketing.auditoriums.dto.toResponse
import io.github.bernardusz.booking_ticketing.languages.dto.LanguageResponse
import io.github.bernardusz.booking_ticketing.languages.dto.toResponse
import io.github.bernardusz.booking_ticketing.movies.dto.MovieSummary
import io.github.bernardusz.booking_ticketing.movies.dto.toSummary
import io.github.bernardusz.booking_ticketing.showings.Showing
import java.math.BigDecimal
import java.time.OffsetDateTime

data class ShowingDetailResponse(
    val id: Long,
    val startTime: OffsetDateTime,
    val price: BigDecimal,
    val movie: MovieSummary,
    val auditorium: AuditoriumResponse,
    val language: LanguageResponse,
    val seats: List<ShowingSeatResponse>
)

data class ShowingSeatResponse(
    val ticketId: Long,
    val seatId: Long,
    val rowLabel: String,
    val seatNumber: Int,
    val status: String // AVAILABLE, LOCKED, BOOKED
)

fun Showing.toDetailResponse(): ShowingDetailResponse = ShowingDetailResponse(
    id = this.id,
    startTime = this.startTime,
    price = this.price,
    movie = this.movie.toSummary(),
    auditorium = this.auditorium.toResponse(),
    language = this.language.toResponse(),
    seats = this.tickets.map { ticket ->
        ShowingSeatResponse(
            ticketId = ticket.id,
            seatId = ticket.seat.id,
            rowLabel = ticket.seat.rowLabel,
            seatNumber = ticket.seat.seatNumber,
            status = ticket.status.name
        )
    }
)
package io.github.bernardusz.booking_ticketing.showings

import io.github.bernardusz.booking_ticketing.auditoriums.AuditoriumRepository
import io.github.bernardusz.booking_ticketing.languages.LanguageRepository
import io.github.bernardusz.booking_ticketing.movies.MovieRepository
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.external.ScheduleCollisionException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.AuditoriumNotFound
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.MovieNotFoundException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.ShowingNotFoundException
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingSaveRequest
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingDetailResponse
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingSummaryResponse
import io.github.bernardusz.booking_ticketing.showings.dto.fetchRelations
import io.github.bernardusz.booking_ticketing.showings.dto.toDetailResponse
import io.github.bernardusz.booking_ticketing.showings.dto.toSummary
import io.github.bernardusz.booking_ticketing.tickets.Ticket
import io.github.bernardusz.booking_ticketing.tickets.TicketRepository
import io.github.bernardusz.booking_ticketing.tickets.TicketStatus
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class ShowingService(
    private val showingRepository: ShowingRepository,
    private val movieRepository: MovieRepository,
    private val auditoriumRepository: AuditoriumRepository,
    private val languageRepository: LanguageRepository,
    private val ticketRepository: TicketRepository
) {
    @Transactional
    fun createShowing(
        showingSaveRequest: ShowingSaveRequest
    ): ShowingDetailResponse {
        val movie = movieRepository.findById(
            showingSaveRequest.movieId
        ).orElseThrow {
            MovieNotFoundException("Movie with id ${showingSaveRequest.movieId} not found")
        }
        val auditorium = auditoriumRepository.findByIdWithSeats(
            showingSaveRequest.auditoriumId
        ) ?: throw AuditoriumNotFound(
            "Auditorium with ${showingSaveRequest.auditoriumId} not found",
        )
        val languageRef = languageRepository.getReferenceById(
            showingSaveRequest.languageCode.uppercase()
        )

        val startTime: OffsetDateTime = showingSaveRequest.startTime
        val endTime: OffsetDateTime = startTime
            .plusMinutes(
                movie.durationMinutes.toLong()
            )

        val isOverlapping = showingRepository.existsOverlappingShowing(
            auditoriumId = showingSaveRequest.auditoriumId,
            startTime = startTime,
            endTime = endTime
        )

        if (isOverlapping){
                throw ScheduleCollisionException(
                    "Schedule collision: Auditorium ${showingSaveRequest.auditoriumId} is already occupied during $startTime - $endTime"
                )
        }

        val newShowing = Showing(
            movie = movie,
            startTime = showingSaveRequest.startTime,
            language = languageRef,
            price = showingSaveRequest.price,
            auditorium = auditorium,
        )

        val showing = showingRepository.save(newShowing)

        val tickets = auditorium.seats.map { seat ->
            Ticket(
                showing = showing,
                seat = seat,
                status = TicketStatus.AVAILABLE
            )
        }

        ticketRepository.saveAll(tickets)

        return showing.toDetailResponse()
    }

    @Transactional(readOnly = true)
    fun searchShowings(
        languageCodes: List<String>?,
        startTime: OffsetDateTime?,
        endTime: OffsetDateTime?,
        movieId: Long?,
        auditoriumId: Long?
    ): List<ShowingSummaryResponse> {

        val activeSpec = listOfNotNull(
            ShowingSpecification.isInLanguage(languageCodes),
            ShowingSpecification.startsAfter(startTime),
            ShowingSpecification.startsBefore(endTime),
            ShowingSpecification.hasMovieId(movieId),
            ShowingSpecification.hasAuditoriumId(auditoriumId),
            ShowingSpecification.fetchRelations()
        )

        val spec = Specification.allOf(activeSpec)

        return showingRepository.findAll(spec)
            .map { it.toSummary() }
    }

    @Transactional(readOnly = true)
    fun getShowing(id: Long): ShowingDetailResponse {
        val showing: Showing = showingRepository.findByIdWithAllRelations(id) ?:
            throw ShowingNotFoundException(
                "Showing with id $id not found",
            )

        return showing.toDetailResponse()
    }

    @Transactional
    fun updateShowing(
        id: Long,
        showingSaveRequest: ShowingSaveRequest
    ): ShowingDetailResponse {
        val showing: Showing = showingRepository.findByIdWithAllRelations(id)
            ?: throw ShowingNotFoundException(
                "Showing with id $id not found",
            )

        val movieRef = movieRepository.getReferenceById(
            showingSaveRequest.movieId
        )
        val auditoriumRef = auditoriumRepository.getReferenceById(
            showingSaveRequest.auditoriumId
        )
        val languageRef = languageRepository.getReferenceById(
            showingSaveRequest.
                languageCode
                    .uppercase()
                    .trim()
        )

        showing.movie = movieRef
        showing.startTime = showingSaveRequest.startTime
        showing.language = languageRef
        showing.price = showingSaveRequest.price
        showing.auditorium = auditoriumRef

        return showing.toDetailResponse()
    }

    @Transactional
    fun deleteShowing(id: Long) {
        if (!showingRepository.existsById(id)) {
            throw ShowingNotFoundException(
                "Showing with id $id not found",
            )
        }

        showingRepository.deleteById(id)
    }
}
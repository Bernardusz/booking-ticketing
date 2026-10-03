package io.github.bernardusz.booking_ticketing.showings

import io.github.bernardusz.booking_ticketing.auditoriums.Auditorium
import io.github.bernardusz.booking_ticketing.languages.Language
import io.github.bernardusz.booking_ticketing.movies.Movie
import org.springframework.data.jpa.domain.Specification
import java.time.OffsetDateTime

object ShowingSpecification {
    fun isInLanguage(languageCodes: List<String>?): Specification<Showing>? {
        if (languageCodes.isNullOrEmpty()) return null

        return Specification { root, _, _ ->
            val uppercaseCodes = languageCodes.map { it.uppercase().trim() }
            root.get<Language>("language").get<String>("code").`in`(uppercaseCodes)
        }
    }

    fun startsAfter(startTime: OffsetDateTime?): Specification<Showing>? {
        return startTime?.let {
            Specification { root, _, cb -> cb.greaterThanOrEqualTo(root.get("startTime"), it) }
        }
    }

    fun startsBefore(endTime: OffsetDateTime?): Specification<Showing>? {
        return endTime?.let {
            Specification { root, _, cb -> cb.lessThanOrEqualTo(root.get("startTime"), it) }
        }
    }

    fun hasMovieId(movieId: Long?): Specification<Showing>? {
        return movieId?.let {
            Specification { root, _, cb -> cb.equal(root.get<Movie>("movie").get<Long>("id"), it) }
        }
    }

    fun hasAuditoriumId(auditoriumId: Long?): Specification<Showing>? {
        return auditoriumId?.let {
            Specification { root, _, cb -> cb.equal(root.get<Auditorium>("auditorium").get<Long>("id"), it) }
        }
    }
}

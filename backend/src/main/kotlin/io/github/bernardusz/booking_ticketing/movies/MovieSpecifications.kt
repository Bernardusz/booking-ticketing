package io.github.bernardusz.booking_ticketing.movies

import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import jakarta.persistence.criteria.Predicate

object MovieSpecifications {
    fun hasMaxDuration(maxDuration: Int?): Specification<Movie>? {
        return maxDuration?.let {
            Specification { root, _, cb -> cb.lessThanOrEqualTo(root.get("durationMinutes"), it) }
        }
    }

    fun hasMinDuration(minDuration: Int?): Specification<Movie>? {
        return minDuration?.let {
            Specification { root, _, cb -> cb.greaterThanOrEqualTo(root.get("durationMinutes"), it) }
        }
    }

    fun isReleasedAfter(date: LocalDate?): Specification<Movie>? {
        return date?.let {
            Specification { root, _, cb -> cb.greaterThanOrEqualTo(root.get("releaseDate"), it) }
        }
    }

    fun isReleasedBefore(date: LocalDate?): Specification<Movie>? {
        return date?.let {
            Specification { root, _, cb -> cb.lessThanOrEqualTo(root.get("releaseDate"), it) }
        }
    }

    fun containsTitleOrDescription(keyword: String?): Specification<Movie>? {
        return keyword?.takeIf { it.isNotBlank() }?.let { search ->
            Specification {root, _, cb ->
                val pattern: String = "%${search.lowercase()}%"

                val titlePredicate: Predicate = cb.like(
                    cb.lower(root.get("title")),
                    pattern
                )

                val descriptionPredicate: Predicate = cb.like(
                    cb.lower(root.get("description")),
                    pattern
                )

                cb.or(
                    titlePredicate,
                    descriptionPredicate
                )
            }
        }
    }

}
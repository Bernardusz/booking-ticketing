package io.github.bernardusz.booking_ticketing.movies

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.Optional


interface MovieRepository: JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    // Title Finding
    fun existsByTitle(title: String): Boolean
    fun findByTitle(title: String): Optional<Movie>

    // Find All by Total Duration
//    fun findByDurationMinutesLessThan(duration: Int): List<Movie>
//    fun findByDurationMinutesGreaterThan(duration: Int): List<Movie>
//
//    // Find All By Release Date
//    fun findAllByOrderByReleaseDateDesc(): List<Movie>
//    fun findByReleaseDateAfter(date: LocalDate): List<Movie>
//    fun findByReleaseDateBefore(date: LocalDate): List<Movie>
//
//    // Find All by Title or Description Search
//    @Query(
//        """
//            SELECT m FROM Movie m
//            WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%'))
//                OR LOWER(m.description) LIKE LOWER(CONCAT('%', :query, '%'))
//        """
//    )
//    fun searchByTitleOrDescription(query: String): List<Movie>
}
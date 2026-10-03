package io.github.bernardusz.booking_ticketing.showings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ShowingRepository : JpaRepository<Showing, Long>, JpaSpecificationExecutor<Showing> {
    @Suppress("SqlResolve")
    @Query(
        value = """
        SELECT EXISTS (
            SELECT 1 
            FROM showings s
            JOIN movies m ON s.movie_id = m.id
            WHERE s.auditorium_id = :auditoriumId
              AND s.start_time < :endTime
              AND (s.start_time + (m.duration_minutes || ' minutes')::INTERVAL) > :startTime
        )
    """,
        nativeQuery = true
    )
    fun existsOverlappingShowing(
        @Param("auditoriumId") auditoriumId: Long,
        @Param("startTime") startTime: OffsetDateTime,
        @Param("endTime") endTime: OffsetDateTime
    ): Boolean

    @Query("""
        SELECT s FROM Showing s
            LEFT JOIN FETCH s.movie
            LEFT JOIN FETCH s.auditorium 
            LEFT JOIN FETCH s.language 
            LEFT JOIN FETCH s.tickets t 
            LEFT JOIN FETCH t.seat 
            WHERE s.id = :id
        """)
    fun findByIdWithAllRelations(@Param("id") id: Long): Showing?
}
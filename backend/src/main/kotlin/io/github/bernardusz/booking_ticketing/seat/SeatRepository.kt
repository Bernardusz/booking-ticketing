package io.github.bernardusz.booking_ticketing.seat

import org.springframework.data.jpa.repository.JpaRepository

interface SeatRepository : JpaRepository<Seat, Long> {
    // Fetch all seats for a specific auditorium ordered by row and seat number
    fun findAllByAuditoriumIdOrderByRowLabelAscSeatNumberAsc(auditoriumId: Long): List<Seat>
}
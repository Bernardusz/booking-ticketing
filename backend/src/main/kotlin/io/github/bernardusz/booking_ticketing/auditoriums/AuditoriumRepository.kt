package io.github.bernardusz.booking_ticketing.auditoriums

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AuditoriumRepository: JpaRepository<Auditorium, Long>{
    @Query(
        """
            SELECT a FROM Auditorium a
            WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :identifier, '%')) OR
                LOWER(a.code) LIKE LOWER(CONCAT('%', :identifier, '%'))
        """
    )
    fun searchByNameOrCode(identifier: String): List<Auditorium>
}
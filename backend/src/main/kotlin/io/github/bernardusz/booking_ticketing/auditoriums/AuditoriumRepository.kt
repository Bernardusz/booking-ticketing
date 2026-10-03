package io.github.bernardusz.booking_ticketing.auditoriums

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AuditoriumRepository: JpaRepository<Auditorium, Long>{
    fun existsByCode(code: String): Boolean
    @Query(
        """
            SELECT a FROM Auditorium a
            WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :identifier, '%')) OR
                LOWER(a.code) LIKE LOWER(CONCAT('%', :identifier, '%'))
        """
    )
    fun searchByNameOrCode(identifier: String): List<Auditorium>

    @Query("SELECT a FROM Auditorium a LEFT JOIN FETCH a.seats WHERE a.id = :id")
    fun findByIdWithSeats(@Param("id") id: Long): Auditorium?
}
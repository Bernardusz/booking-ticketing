package io.github.bernardusz.booking_ticketing.tickets

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime

interface TicketRepository : JpaRepository<Ticket, Long> {

    fun findAllByShowingId(showingId: Long): List<Ticket>

    // Lock seats: Only update if current status is AVAILABLE OR (LOCKED and expired)
    @Modifying
    @Query("""
        UPDATE Ticket t 
        SET t.status = 'LOCKED', 
            t.user = (SELECT u FROM User u WHERE u.id = :userId), 
            t.lockExpiration = :lockExpiration
        WHERE t.id IN :ticketIds 
          AND (t.status = 'AVAILABLE' OR (t.status = 'LOCKED' AND t.lockExpiration < :now))
    """)
    fun lockTickets(
        @Param("ticketIds") ticketIds: List<Long>,
        @Param("userId") userId: Long,
        @Param("lockExpiration") lockExpiration: OffsetDateTime,
        @Param("now") now: OffsetDateTime
    ): Int

    // Confirm booking: Only update if ticket is LOCKED by THIS user and lock hasn't expired
    @Modifying
    @Query("""
        UPDATE Ticket t 
        SET t.status = 'BOOKED', 
            t.lockExpiration = NULL 
        WHERE t.id IN :ticketIds 
          AND t.user.id = :userId 
          AND t.status = 'LOCKED' 
          AND t.lockExpiration >= :now
    """)
    fun bookTickets(
        @Param("ticketIds") ticketIds: List<Long>,
        @Param("userId") userId: Long,
        @Param("now") now: OffsetDateTime
    ): Int

    // Reset expired locks back to AVAILABLE (Used by Scheduler)
    @Modifying
    @Query("""
        UPDATE Ticket t 
        SET t.status = 'AVAILABLE', 
            t.user = NULL, 
            t.lockExpiration = NULL 
        WHERE t.status = 'LOCKED' 
          AND t.lockExpiration < :now
    """)
    fun releaseExpiredLocks(@Param("now") now: OffsetDateTime): Int
}
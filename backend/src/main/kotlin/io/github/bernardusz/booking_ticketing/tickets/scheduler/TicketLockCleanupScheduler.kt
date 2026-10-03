package io.github.bernardusz.booking_ticketing.tickets.scheduler

import io.github.bernardusz.booking_ticketing.tickets.TicketRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Component
class TicketLockCleanupScheduler(
    private val ticketRepository: TicketRepository
) {

    @Scheduled(fixedDelay = 60000) // Every 60 seconds
    @Transactional
    fun cleanupExpiredLocks() {
        val releasedCount = ticketRepository.releaseExpiredLocks(OffsetDateTime.now())
        if (releasedCount > 0) {
            println("Released $releasedCount expired ticket locks back to AVAILABLE.")
        }
    }
}
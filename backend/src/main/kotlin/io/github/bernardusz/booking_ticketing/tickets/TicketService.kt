package io.github.bernardusz.booking_ticketing.tickets

import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed.AlreadyExistsException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed.BookingAlreadyExistException
import io.github.bernardusz.booking_ticketing.tickets.dto.TicketBookingRequest
import io.github.bernardusz.booking_ticketing.tickets.dto.TicketLockRequest
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class TicketService(
    private val ticketRepository: TicketRepository
) {
    @Transactional
    fun lockSeats(
        request: TicketLockRequest,
        userId: Long
    ){
        val now = OffsetDateTime.now()
        val expiration = now.plusMinutes(10)

        val updatedCount = ticketRepository.lockTickets(
            ticketIds = request.ticketIds,
            userId = userId,
            lockExpiration = expiration,
            now = now
        )

        if (updatedCount != request.ticketIds.size) {
            throw BookingAlreadyExistException("One or more selected seats are no longer available")
        }
    }

    @Transactional
    fun bookSeats(request: TicketBookingRequest, userId: Long) {
        val now = OffsetDateTime.now()

        val updatedCount = ticketRepository.bookTickets(
            ticketIds = request.ticketIds,
            userId = userId,
            now = now
        )

        if (updatedCount != request.ticketIds.size) {
            throw BookingAlreadyExistException("Your seat lock expired or tickets belong to another user")
        }
    }
}
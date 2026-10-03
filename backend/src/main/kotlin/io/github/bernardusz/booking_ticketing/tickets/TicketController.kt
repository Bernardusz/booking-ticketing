package io.github.bernardusz.booking_ticketing.tickets

import io.github.bernardusz.booking_ticketing.tickets.dto.TicketBookingRequest
import io.github.bernardusz.booking_ticketing.tickets.dto.TicketLockRequest
import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tickets")
class TicketController(
    private val ticketService: TicketService
) {

    @PostMapping("/lock")
    fun lockTickets(
        @Valid @RequestBody request: TicketLockRequest,
        @AuthenticationPrincipal userSecurity: UserSecurity
    ): ResponseEntity<Void> {
        ticketService.lockSeats(request, userSecurity.getId())
        return ResponseEntity.ok().build()
    }

    @PostMapping("/book")
    fun bookTickets(
        @Valid @RequestBody request: TicketBookingRequest,
        @AuthenticationPrincipal userSecurity: UserSecurity
    ): ResponseEntity<Void> {
        ticketService.bookSeats(request, userSecurity.getId())
        return ResponseEntity.ok().build()
    }
}
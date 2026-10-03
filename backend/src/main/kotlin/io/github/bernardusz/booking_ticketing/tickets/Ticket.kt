package io.github.bernardusz.booking_ticketing.tickets

import io.github.bernardusz.booking_ticketing.seat.Seat
import io.github.bernardusz.booking_ticketing.showings.Showing
import io.github.bernardusz.booking_ticketing.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "tickets")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: TicketStatus,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showing_id", nullable = false)
    val showing: Showing,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    val seat: Seat,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @Column(nullable = true)
    val lockExpiration: OffsetDateTime? = null,
) {
}
package io.github.bernardusz.booking_ticketing.auditoriums

import io.github.bernardusz.booking_ticketing.seat.Seat
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingSeatResponse
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "auditoriums")
class Auditorium(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var code: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var totalSeats: Int,

    @OneToMany(
        mappedBy = "auditorium",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var seats: List<Seat> = mutableListOf(),
)
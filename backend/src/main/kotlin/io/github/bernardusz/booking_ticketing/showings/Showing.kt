package io.github.bernardusz.booking_ticketing.showings

import io.github.bernardusz.booking_ticketing.auditoriums.Auditorium
import io.github.bernardusz.booking_ticketing.languages.Language
import io.github.bernardusz.booking_ticketing.movies.Movie
import io.github.bernardusz.booking_ticketing.tickets.Ticket
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity
@Table(name = "showings")
class Showing(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    var movie: Movie,

    @Column(nullable = false)
    var startTime: OffsetDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_code", nullable = false)
    var language: Language,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auditorium_id", nullable = false)
    var auditorium: Auditorium,

    @OneToMany(
        mappedBy = "showing",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var tickets: MutableList<Ticket> = mutableListOf()
)
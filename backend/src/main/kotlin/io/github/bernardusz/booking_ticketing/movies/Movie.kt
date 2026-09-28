package io.github.bernardusz.booking_ticketing.movies

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
    name = "movies"
)
class Movie(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var releaseDate: LocalDate,

    @Column(nullable = false)
    var durationMinutes: Int,

    @Column(nullable = false)
    var posterUrl: String,
)
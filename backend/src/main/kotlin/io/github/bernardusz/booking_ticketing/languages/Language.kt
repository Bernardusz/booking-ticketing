package io.github.bernardusz.booking_ticketing.languages

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "languages")
class Language(
    @Id
    @Column(length = 10, nullable = false, updatable = false)
    val code: String,

    @Column(length = 50, nullable = false, unique = true)
    var title: String
)
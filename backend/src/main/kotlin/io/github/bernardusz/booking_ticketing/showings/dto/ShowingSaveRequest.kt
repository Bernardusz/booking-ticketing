package io.github.bernardusz.booking_ticketing.showings.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class ShowingSaveRequest(
    val movieId: Long,
    val startTime: OffsetDateTime,
    val languageCode: String,
    val price: BigDecimal,
    val auditoriumId: Long,
)

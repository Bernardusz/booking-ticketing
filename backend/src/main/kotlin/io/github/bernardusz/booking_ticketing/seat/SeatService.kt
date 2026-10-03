package io.github.bernardusz.booking_ticketing.seat

import io.github.bernardusz.booking_ticketing.auditoriums.AuditoriumRepository
import io.github.bernardusz.booking_ticketing.seat.dto.SeatResponse
import io.github.bernardusz.booking_ticketing.seat.dto.toResponse
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.AuditoriumNotFound
import org.springframework.stereotype.Service

@Service
class SeatService(
    private val seatRepository: SeatRepository,
    private val auditoriumRepository: AuditoriumRepository
) {

    fun getSeatsByAuditoriumId(
        auditoriumId: Long
    ): List<SeatResponse>{
        if (!auditoriumRepository.existsById(auditoriumId)){
            throw AuditoriumNotFound("Auditorium with id $auditoriumId not found")
        }

        return seatRepository.findAllByAuditoriumIdOrderByRowLabelAscSeatNumberAsc(
            auditoriumId
        ).map {
            it.toResponse()
        }
    }
}
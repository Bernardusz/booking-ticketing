package io.github.bernardusz.booking_ticketing.auditoriums

import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumResponse
import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumSaveRequest
import io.github.bernardusz.booking_ticketing.auditoriums.dto.toResponse
import io.github.bernardusz.booking_ticketing.seat.Seat
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed.AuditoriumAlreadyExistException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.AuditoriumNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuditoriumService(
    private val auditoriumRepository: AuditoriumRepository
) {
    @Transactional
    fun createAuditorium(
        request: AuditoriumSaveRequest
    ): Long {
        val cleanCode = request.code.uppercase().trim()

        if (auditoriumRepository.existsByCode(cleanCode)) {
            throw AuditoriumAlreadyExistException(
                "Auditorium already exists with code $cleanCode"
            )
        }
        val auditorium = Auditorium(
            name = request.name,
            code = request.code,
            totalSeats = request.totalSeats
        )

        val generatedSeats = mutableListOf<Seat>()
        for (rowIndex in 0 until request.rowsCount) {
            val rowLabel = ('A' + rowIndex).toString() // Generates 'A', 'B', 'C', etc.
            for (seatNum in 1..request.seatsPerRow) {
                generatedSeats.add(
                    Seat(
                        rowLabel = rowLabel,
                        seatNumber = seatNum,
                        auditorium = auditorium
                    )
                )
            }
        }

        auditorium.seats = generatedSeats

        val savedAuditorium = auditoriumRepository.save(auditorium)

        return savedAuditorium.id
    }

    @Transactional(readOnly = true)
    fun getAuditoriums(): List<AuditoriumResponse>{
        return auditoriumRepository.findAll().map {
            it.toResponse()
        }
    }

    @Transactional(readOnly = true)
    fun getAuditoriumByNameOrCode(
        identifier: String
    ): List<AuditoriumResponse>{
        return auditoriumRepository.searchByNameOrCode(
            identifier = identifier
        ).map {
            it.toResponse()
        }
    }

    @Transactional(readOnly = true)
    fun getAuditoriumById(id: Long): AuditoriumResponse{
        return auditoriumRepository.findById(id)
            .orElseThrow {
                AuditoriumNotFound(
                    "Auditorium with id $id not found",
                )
            }.toResponse()
    }

    @Transactional
    fun updateAuditorium(
        id: Long,
        auditoriumSaveRequest: AuditoriumSaveRequest
    ): AuditoriumResponse {
        val auditorium: Auditorium = auditoriumRepository.findById(id)
            .orElseThrow {
                AuditoriumNotFound(
                    "Auditorium with id $id not found",
                )
            }

        auditorium.name = auditoriumSaveRequest.name
        auditorium.code = auditoriumSaveRequest.code
        auditorium.totalSeats = auditoriumSaveRequest.totalSeats

        return auditorium.toResponse()
    }

    @Transactional
    fun deleteAuditoriumById(id: Long) {
        if (!auditoriumRepository.existsById(id)){
            throw AuditoriumNotFound(
                "Auditorium with id $id not found",
            )
        }
        auditoriumRepository.deleteById(id)
    }
}
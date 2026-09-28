package io.github.bernardusz.booking_ticketing.auditoriums

import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumResponse
import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumSaveRequest
import io.github.bernardusz.booking_ticketing.auditoriums.dto.toResponse
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.AuditoriumNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuditoriumService(
    private val auditoriumRepository: AuditoriumRepository
) {
    @Transactional
    fun createAuditorium(
        auditorium: AuditoriumSaveRequest
    ): Long {
        val newAuditorium = Auditorium(
            name = auditorium.name,
            code = auditorium.code,
            totalSeats = auditorium.totalSeats
        )

        return auditoriumRepository.save(newAuditorium).id
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
                    404
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
                    404
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
                404
            )
        }
        auditoriumRepository.deleteById(id)
    }
}
package io.github.bernardusz.booking_ticketing.auditoriums

import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumResponse
import io.github.bernardusz.booking_ticketing.auditoriums.dto.AuditoriumSaveRequest
import io.github.bernardusz.booking_ticketing.seat.SeatService
import io.github.bernardusz.booking_ticketing.seat.dto.SeatResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/auditoriums")
class AuditoriumController(
    val auditoriumService: AuditoriumService,
    val seatService: SeatService
) {
    @PostMapping
    fun create(
        @RequestBody
        auditoriumSaveRequest: AuditoriumSaveRequest
    ): ResponseEntity<Void> {
        val auditoriumId: Long = auditoriumService
            .createAuditorium(
                auditoriumSaveRequest
            )

        return ResponseEntity.created(
            URI.create("/api/v1/auditoriums/$auditoriumId")
        ).build()
    }

    @GetMapping
    fun getAuditoriums(
        @RequestParam(required = false) identifier: String?,
    ): List<AuditoriumResponse> {
        return if (identifier == null){
            auditoriumService.getAuditoriums()
        } else {
            auditoriumService.getAuditoriumByNameOrCode(
                identifier = identifier,
            )
        }
    }

    @GetMapping("/{auditoriumId}")
    fun getAuditoriumById(
        @PathVariable("auditoriumId") auditoriumId: Long
    ): ResponseEntity<AuditoriumResponse> {
        val auditorium: AuditoriumResponse = auditoriumService
            .getAuditoriumById(
                id = auditoriumId
            )
        return ResponseEntity.ok(auditorium)
    }

    @PutMapping("/{auditoriumId}")
    fun updateAuditoriumById(
        @PathVariable("auditoriumId") auditoriumId: Long,
        @RequestBody auditoriumSaveRequest: AuditoriumSaveRequest
    ): ResponseEntity<AuditoriumResponse> {
        val auditorium: AuditoriumResponse = auditoriumService
            .updateAuditorium(
                id = auditoriumId,
                auditoriumSaveRequest = auditoriumSaveRequest
            )

        return ResponseEntity.ok(auditorium)
    }

    @DeleteMapping("/{auditoriumId}")
    fun deleteAuditoriumById(
        @PathVariable("auditoriumId") auditoriumId: Long
    ): ResponseEntity<Void> {
        auditoriumService.deleteAuditoriumById(
            id = auditoriumId
        )

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{auditoriumId}/seats")
    fun getAuditoriumSeats(
        @PathVariable auditoriumId: Long
    ): ResponseEntity<List<SeatResponse>> {
        val seats = seatService.getSeatsByAuditoriumId(auditoriumId)
        return ResponseEntity.ok(seats)
    }
}
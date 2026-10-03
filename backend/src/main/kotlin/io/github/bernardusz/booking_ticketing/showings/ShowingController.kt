package io.github.bernardusz.booking_ticketing.showings

import io.github.bernardusz.booking_ticketing.movies.dto.MovieSummary
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingDetailResponse
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingSaveRequest
import io.github.bernardusz.booking_ticketing.showings.dto.ShowingSummaryResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
import java.time.LocalDateTime
import java.time.OffsetDateTime

@RestController
@RequestMapping("/api/v1/showings")
class ShowingController(
    private val showingService: ShowingService,
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createShowing(
        @Valid @RequestBody showingSaveRequest: ShowingSaveRequest
    ): ResponseEntity<Void>{
        val showingId: Long = showingService
            .createShowing(showingSaveRequest)
            .id

        return ResponseEntity.created(
            URI.create("/api/v1/showings/$showingId")
        ).build()
    }

    @GetMapping
    fun getShowings(
        @RequestParam(required = false) languageCode: List<String>?,
        @RequestParam(required = false) startTime: OffsetDateTime?,
        @RequestParam(required = false) endTime: OffsetDateTime?,
        @RequestParam(required = false) movieId: Long?,
        @RequestParam(required = false) auditoriumId: Long?
    ): ResponseEntity<List<ShowingSummaryResponse>> {
        val showings: List<ShowingSummaryResponse>
            = showingService
                .searchShowings(
                    languageCodes = languageCode,
                    startTime = startTime,
                    endTime = endTime,
                    movieId = movieId,
                    auditoriumId = auditoriumId
        )


        return ResponseEntity.ok(
            showings
        )
    }

    @GetMapping("/{showingId}")
    fun getShowing(
        @PathVariable("showingId") showingId: Long,
    ): ResponseEntity<ShowingDetailResponse> {
        return ResponseEntity.ok(
            showingService.getShowing(showingId)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{showingId}")
    fun updateShowing(
        @PathVariable("showingId") showingId: Long,
        @Valid @RequestBody showingSaveRequest: ShowingSaveRequest
    ): ResponseEntity<ShowingDetailResponse> {
        val showingDetailResponse: ShowingDetailResponse =
            showingService.updateShowing(
                id = showingId,
                showingSaveRequest = showingSaveRequest
            )

        return ResponseEntity.ok(
            showingDetailResponse
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{showingId}")
    fun deleteShowing(
        @PathVariable("showingId") showingId: Long,
    ): ResponseEntity<Void> {
        showingService.deleteShowing(
            id = showingId
        )

        return ResponseEntity.noContent().build()
    }

}
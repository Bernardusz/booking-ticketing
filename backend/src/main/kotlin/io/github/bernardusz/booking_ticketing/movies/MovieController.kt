package io.github.bernardusz.booking_ticketing.movies

import io.github.bernardusz.booking_ticketing.movies.dto.MovieDetails
import io.github.bernardusz.booking_ticketing.movies.dto.MovieSaveRequest
import io.github.bernardusz.booking_ticketing.movies.dto.MovieSummary
import org.springframework.format.annotation.DateTimeFormat
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
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/movies")
class MovieController(
    private val movieService: MovieService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createMovie(@RequestBody movie: MovieSaveRequest)
        : ResponseEntity<Void> {
        val movieId: Long = movieService.createMovie(movie)
        return ResponseEntity.created(
            URI.create("/api/v1/movies/$movieId")
        ).build()
    }

    @GetMapping
    fun getAllMovies(
        @RequestParam(required = false) maxDuration: Int?,
        @RequestParam(required = false) minDuration: Int?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) releasedAfter: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) releasedBefore: LocalDate?,
        @RequestParam(required = false) keywords: String?,
    ): ResponseEntity<List<MovieSummary>> {
        val movies: List<MovieSummary> = movieService.filterMovies(
            maxDuration = maxDuration,
            minDuration = minDuration,
            releasedAfter = releasedAfter,
            releasedBefore = releasedBefore,
            keywords = keywords,
        )
        return ResponseEntity.ok(movies)
    }

    @GetMapping("/{movieId}")
    fun getMovieById(
        @PathVariable movieId: Long,
    ): MovieDetails{
        return movieService.findById(movieId)
    }

    @GetMapping("/title/{title}")
    fun getMovieByTitle(
        @PathVariable title: String,
    ): MovieDetails{
        return movieService.findByTitle(title)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{movieId}")
    fun updateMovie(
        @RequestBody movieSaveRequest: MovieSaveRequest,
        @PathVariable movieId: Long,
    ): ResponseEntity<MovieDetails> {
        val movie: MovieDetails = movieService.updateMovie(
            id = movieId,
            movieSaveRequest = movieSaveRequest
        )

        return ResponseEntity.ok(movie)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{movieId}")
    fun deleteMovie(
        @PathVariable movieId: Long,
    ): ResponseEntity<Void> {
        movieService.deleteById(movieId)

        return ResponseEntity.noContent().build()
    }
}
package io.github.bernardusz.booking_ticketing.movies

import io.github.bernardusz.booking_ticketing.movies.dto.MovieDetails
import io.github.bernardusz.booking_ticketing.movies.dto.MovieSaveRequest
import io.github.bernardusz.booking_ticketing.movies.dto.MovieSummary
import io.github.bernardusz.booking_ticketing.movies.dto.toDetails
import io.github.bernardusz.booking_ticketing.movies.dto.toSummary
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed.MovieAlreadyExistException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.MovieNotFoundException
import io.github.bernardusz.booking_ticketing.shared.util.PosterUrlGenerator
import org.springframework.beans.factory.BeanRegistry
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MovieService(
    private val movieRepository: MovieRepository,
    private val posterUrlGenerator: PosterUrlGenerator
) {
    // CREATE
    fun createMovie(movie: MovieSaveRequest): Long {
        if (movieRepository.existsByTitle(movie.title)){
            throw MovieAlreadyExistException("Movie with title ${movie.title} already exists")
        }

        val newMovie = Movie(
            title = movie.title,
            description = movie.description,
            releaseDate = movie.releaseDate,
            durationMinutes = movie.durationMinutes,
            posterUrl = posterUrlGenerator.generateRandomPosterUrl()
        )

        return movieRepository.save(
            newMovie
        ).id
    }

    // GET: List
    fun getMovies(): List<MovieSummary> {
        return movieRepository.findAll().map {
            it.toSummary()
        }
    }

    fun filterMovies(
        maxDuration: Int? = null,
        minDuration: Int? = null,
        releasedAfter: LocalDate? = null,
        releasedBefore: LocalDate? = null,
        keywords: String? = null,
    ): List<MovieSummary> {
        val activeSpecs = listOfNotNull(
            MovieSpecifications.hasMaxDuration(maxDuration),
            MovieSpecifications.hasMinDuration(minDuration),
            MovieSpecifications.isReleasedAfter(releasedAfter),
            MovieSpecifications.isReleasedBefore(releasedBefore),
            MovieSpecifications.containsTitleOrDescription(keywords)
        )

        val spec: Specification<Movie> = Specification.allOf(activeSpecs)

        val sort = Sort.by(Sort.Direction.DESC, "releaseDate")
        return movieRepository.findAll(spec, sort).map { it.toSummary() }
    }

    // GET: Singular
    fun findById(id: Long): MovieDetails{
        return movieRepository.findById(
            id
        ).orElseThrow {
            MovieNotFoundException(
                "Movie with the id $id is not found"
            )
        }.toDetails()
    }

    fun findByTitle(title: String): MovieDetails {
        return movieRepository.findByTitle(title)
            .orElseThrow {
                MovieNotFoundException(
                    "Movie with the title $title is not found"
                )
            }.toDetails()
    }

    // PUT: Update
    fun updateMovie(id: Long, movieSaveRequest: MovieSaveRequest): MovieDetails{
        val movie: Movie = movieRepository.findById(id)
            .orElseThrow {
                MovieNotFoundException(
                    "Movie with the id $id is not found"
                )
            }

        movie.title = movieSaveRequest.title
        movie.description = movieSaveRequest.description
        movie.releaseDate = movieSaveRequest.releaseDate
        movie.durationMinutes = movieSaveRequest.durationMinutes
        movie.posterUrl = posterUrlGenerator.generateRandomPosterUrl()

        return movieRepository.save(movie).toDetails()
    }

    fun deleteById(id: Long){
        if (!movieRepository.existsById(id)) {
            throw MovieNotFoundException("User not found with id: $id")
        }
        movieRepository.deleteById(id)
    }
}
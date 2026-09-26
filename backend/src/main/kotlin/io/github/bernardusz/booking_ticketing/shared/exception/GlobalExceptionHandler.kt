package io.github.bernardusz.booking_ticketing.shared.exception

import io.github.bernardusz.booking_ticketing.shared.exception.dto.ErrorResponse
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.InternalServerException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.RefreshTokenException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    // 1. DTO / Request Body Validation Errors (@Valid failure)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        val fieldError = ex.bindingResult.fieldErrors.firstOrNull()
        val message = fieldError?.let { "${it.field}: ${it.defaultMessage}" }
            ?: "Validation failed for request body."

        val errorResponse = ErrorResponse(
            message = message,
            code = HttpStatus.BAD_REQUEST.value(),
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // 2. Authentication Failures (Invalid credentials, bad tokens, etc.)
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        ex: AuthenticationException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Authentication failed.",
            code = HttpStatus.UNAUTHORIZED.value(),
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    // 3. Authorization / Access Denied (User lacks required role/permissions)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "You do not have permission to access this resource.",
            code = HttpStatus.FORBIDDEN.value(),
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    // 4. Custom Business Exceptions (e.g., UserAlreadyExists, ResourceNotFound)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Invalid request argument.",
            code = HttpStatus.BAD_REQUEST.value(),
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // 5. Catch-All Fallback for Unexpected Internal Server Errors
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = "An unexpected error occurred. Please try again later.",
            code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(InternalServerException::class)
    fun handleInternalServerException(
        ex: InternalServerException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: "An unexpected error occurred. Please try again later.",
            code = ex.code,
            timestamp = LocalDateTime.now()
        )

        return ResponseEntity.status(ex.code).body(errorResponse)
    }

    @ExceptionHandler(RefreshTokenException::class)
    fun handleRefreshTokenException(
        ex: RefreshTokenException
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: "An unexpected error occurred.",
            code = ex.code,
            timestamp = LocalDateTime.now()
        )

        return ResponseEntity.status(ex.code).body(errorResponse)
    }
}
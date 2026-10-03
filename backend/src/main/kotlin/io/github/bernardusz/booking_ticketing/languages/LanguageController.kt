package io.github.bernardusz.booking_ticketing.languages

import io.github.bernardusz.booking_ticketing.languages.dto.LanguageCreateRequest
import io.github.bernardusz.booking_ticketing.languages.dto.LanguageResponse
import io.github.bernardusz.booking_ticketing.languages.dto.LanguageUpdateRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/languages")
class LanguageController(
    private val languageService: LanguageService,
) {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createLanguage(
        @Valid @RequestBody languageCreateRequest: LanguageCreateRequest
    ): ResponseEntity<LanguageResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
        .body(languageService.createLanguage(
            languageCreateRequest
        ))
    }

    @GetMapping
    fun getAllLanguages():
            ResponseEntity<List<LanguageResponse>>{
        return ResponseEntity.ok(
            languageService.getAllLanguages()
        )
    }

    @GetMapping("/{code}")
    fun getLanguageByCode(
        @PathVariable code: String,
    ): ResponseEntity<LanguageResponse> {
        return ResponseEntity.ok(
            languageService.getLanguageByCode(code)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{code}")
    fun updateLanguage(
        @PathVariable code: String,
        @Valid @RequestBody language: LanguageUpdateRequest
    ): ResponseEntity<LanguageResponse> {
        return ResponseEntity.ok(
            languageService.updateLanguage(
                code,
                language
            )
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{code}")
    fun deleteLanguageByCode(
        @PathVariable code: String,
    ): ResponseEntity<Void> {
        languageService.deleteLanguage(code)
        return ResponseEntity.noContent().build()
    }
}
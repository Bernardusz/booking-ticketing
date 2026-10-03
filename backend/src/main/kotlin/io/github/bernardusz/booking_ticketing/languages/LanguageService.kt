package io.github.bernardusz.booking_ticketing.languages

import io.github.bernardusz.booking_ticketing.languages.dto.LanguageCreateRequest
import io.github.bernardusz.booking_ticketing.languages.dto.LanguageResponse
import io.github.bernardusz.booking_ticketing.languages.dto.LanguageUpdateRequest
import io.github.bernardusz.booking_ticketing.languages.dto.toResponse
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed.LanguageAlreadyExistsException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.LanguageNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale.getDefault

@Service
class LanguageService(
    private val languageRepository: LanguageRepository
) {
    @Transactional
    fun createLanguage(request: LanguageCreateRequest): LanguageResponse {
        val cleanCode = request.code.uppercase().trim()

        if (languageRepository.existsById(cleanCode)) {
            throw LanguageAlreadyExistsException("Language with code $cleanCode already exists")
        }

        val language = Language(
            code = cleanCode,
            title = request.title.trim(),
        )

        return languageRepository.save(language).toResponse()
    }

    @Transactional(readOnly = true)
    fun getAllLanguages(): List<LanguageResponse> {
        return languageRepository.findAll().map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getLanguageByCode(code: String): LanguageResponse {
        val cleanCode = code.uppercase(getDefault()).trim()
        return languageRepository.findById(cleanCode)
            .orElseThrow { LanguageNotFoundException("Language with code $cleanCode not found") }
            .toResponse()
    }

    @Transactional
    fun updateLanguage(
        code: String,
        request: LanguageUpdateRequest
    ): LanguageResponse {
        val cleanCode = code.uppercase(getDefault()).trim()

        val language = languageRepository.findById(cleanCode)
            .orElseThrow { LanguageNotFoundException("Language with code $cleanCode not found") }

        language.title = request.title.trim()

        return language.toResponse()
    }

    @Transactional
    fun deleteLanguage(code: String) {
        val cleanCode = code.uppercase().trim()

        if (!languageRepository.existsById(cleanCode)) {
            throw LanguageNotFoundException("Language with code $cleanCode not found")
        }

        languageRepository.deleteById(cleanCode)
    }
}
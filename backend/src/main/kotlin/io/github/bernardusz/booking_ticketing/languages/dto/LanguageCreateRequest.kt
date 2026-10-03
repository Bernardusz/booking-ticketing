package io.github.bernardusz.booking_ticketing.languages.dto

import io.github.bernardusz.booking_ticketing.languages.Language
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LanguageCreateRequest(
    @field:NotBlank(message = "Language code is required")
    @field:Size(min = 2, max = 10, message = "Language code must be between 2 and 10")
    val code: String,

    @field:Size(min = 2, max = 50, message = "Language code must be between 2 and 10")
    val title: String,
)

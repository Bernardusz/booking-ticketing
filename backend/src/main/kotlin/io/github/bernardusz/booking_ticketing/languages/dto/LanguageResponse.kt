package io.github.bernardusz.booking_ticketing.languages.dto

import io.github.bernardusz.booking_ticketing.languages.Language

data class LanguageResponse(
    val code: String,
    val title: String,
)

fun Language.toResponse(): LanguageResponse = LanguageResponse(
    code = this.code,
    title = this.title,
)
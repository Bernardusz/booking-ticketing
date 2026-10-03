package io.github.bernardusz.booking_ticketing.languages

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageRepository : JpaRepository<Language, String>
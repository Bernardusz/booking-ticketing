package io.github.bernardusz.booking_ticketing.user.repository

import io.github.bernardusz.booking_ticketing.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>, CustomUserRepository {
    // Spring Data JPA automatically generates:
    // "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)"
    fun existsByUsername(username: String): Boolean

    // You can also add this for email checks:
    fun existsByEmail(email: String): Boolean
}
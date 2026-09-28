package io.github.bernardusz.booking_ticketing.user.repository

import io.github.bernardusz.booking_ticketing.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long>, CustomUserRepository {
    // Spring Data JPA automatically generates:
    // "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)"
    fun existsByUsername(username: String): Boolean

    // You can also add this for email checks:
    fun existsByEmail(email: String): Boolean

    fun findByUsername(username: String): Optional<User>

    // 2. Find by Email
    fun findByEmail(email: String): Optional<User>

    @Suppress("SqlResolve")
    @Query(
        value ="""
            SELECT * FROM users
            WHERE username ILIKE CONCAT('%', :query ,'%')
                OR email ILIKE CONCAT('%', :query ,'%')
        """,
        nativeQuery = true
    )
    fun searchByUsernameOrEmail(
        query: String
    ): List<User>
}
package io.github.bernardusz.booking_ticketing.user.repository

import io.github.bernardusz.booking_ticketing.user.User
import io.github.bernardusz.booking_ticketing.user.dto.UserInformation
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

interface CustomUserRepository{
    fun findByTicketId(id: Long): UserInformation?
    fun findByIdentifierSecurity(identifier: String): User?
    fun findByRefreshTokenSecurity(refreshTokenHashed: String): User?
}
class CustomUserRepositoryImpl (
    private val jdbcClient: JdbcClient
): CustomUserRepository {
    override fun findByTicketId(ticketId: Long): UserInformation? {
        return jdbcClient.sql(
            """
                SELECT u.id, u.username
                FROM users
                LEFT JOIN tickets t ON u.id = t.user_id
                WHERE t.id = :ticketId
            """.trimIndent()
        ).param("ticketId", ticketId)
        .query(UserInformation::class.java)
        .optional()
        .orElse(null)
    }

    override fun findByIdentifierSecurity(identifier: String): User? {
        return jdbcClient.sql(
            """
                SELECT * FROM users
                WHERE
                    username = :identifier OR
                    email = :identifier
            """.trimIndent()
        ).param("identifier", identifier)
            .query(User::class.java)
            .optional()
            .orElse(null)
    }

    override fun findByRefreshTokenSecurity(refreshTokenHashed: String): User? {
        return jdbcClient.sql(
            """
                SELECT
                  u.id,
                  u.username,
                  u.email,
                  u.password,
                  u.created_at
                FROM users u
                INNER JOIN refresh_tokens rt
                ON rt.user_id = u.id
                WHERE token_hash = :refreshTokenHashed
            """.trimIndent()
        ).param("refreshTokenHashed", refreshTokenHashed)
            .query(User::class.java)
            .optional()
            .orElse(null)
    }
}
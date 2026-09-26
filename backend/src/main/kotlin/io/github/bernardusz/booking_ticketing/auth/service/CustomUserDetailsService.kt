package io.github.bernardusz.booking_ticketing.auth.service

import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import io.github.bernardusz.booking_ticketing.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(identifier: String): UserDetails {
        val user = userRepository.findByIdentifierSecurity(identifier) ?:
            throw UsernameNotFoundException("User not found with identifier: $identifier")

        return UserSecurity(user)
    }

    fun loadUserById(userId: String): UserSecurity {
        val user = userRepository.findById(userId.toLong())
            .orElseThrow { UsernameNotFoundException("User not found with identifier: $userId") }

        return UserSecurity(user)
    }
}
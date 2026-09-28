package io.github.bernardusz.booking_ticketing.user

import io.github.bernardusz.booking_ticketing.auth.service.RefreshTokenService
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.internal.InternalServerException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.external.InvalidPasswordException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.existed.UserAlreadyExistsException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.missing.UserNotFoundException
import io.github.bernardusz.booking_ticketing.shared.util.encodeNonNull
import io.github.bernardusz.booking_ticketing.user.dto.UserCreation
import io.github.bernardusz.booking_ticketing.user.dto.UserInformation
import io.github.bernardusz.booking_ticketing.user.dto.UserInformationUpdate
import io.github.bernardusz.booking_ticketing.user.dto.UserPasswordUpdate
import io.github.bernardusz.booking_ticketing.user.dto.UserRoleUpdate
import io.github.bernardusz.booking_ticketing.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun registerUser(userCreation: UserCreation): Long {
        if (userRepository.existsByUsername(userCreation.username)) {
            throw UserAlreadyExistsException("Username '${userCreation.username}' is already taken.")
        }
        if (userRepository.existsByEmail(userCreation.email)) {
            throw UserAlreadyExistsException("Email '${userCreation.email}' is already registered.")
        }

        val hashedPassword = passwordEncoder.encode(userCreation.password)
            ?: throw InternalServerException("Failed to register user - Password Hashing Error.")

        val newUser = User(
            username = userCreation.username,
            email = userCreation.email,
            password = hashedPassword,
            role = userCreation.role,
        )

        return userRepository.save(newUser).id
    }

    @Transactional(readOnly = true)
    fun getUsers(): List<UserInformation> {
        val users: List<User> = userRepository.findAll()
        return users.map { user ->
            user.toUserInformation()
        }
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserInformation {
        val user: User = userRepository.findById(id)
        .orElseThrow { UserNotFoundException("User not found with id: $id") }

        return user.toUserInformation()
    }

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): UserInformation {
        val user: User = userRepository.findByUsername(username)
        .orElseThrow { UserNotFoundException("User not found with username: $username") }

        return user.toUserInformation()
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): UserInformation {
        val user: User = userRepository.findByEmail(email)
            .orElseThrow { UserNotFoundException("User not found with email: $email") }

        return user.toUserInformation()
    }

    @Transactional(readOnly = true)
    fun searchUserByUsernameAndEmail(query: String): List<UserInformation> {
        val users: List<User> = userRepository.searchByUsernameOrEmail(query)

        return users.map { user ->
            user.toUserInformation()
        }
    }

    @Transactional(readOnly = true)
    fun getUserByTicketId(ticketId: Long): UserInformation {
        return userRepository.findByTicketId(ticketId)
            ?: throw UserNotFoundException("User not found with ticketId: $ticketId")
    }

    @Transactional
    fun updateUserInformation(id: Long, userInformationUpdate: UserInformationUpdate): UserInformation {
        val user: User = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }

        // Check if new username is already taken by someone else
        if (user.username != userInformationUpdate.username &&
            userRepository.existsByUsername(userInformationUpdate.username)) {
            throw UserAlreadyExistsException("Username ${userInformationUpdate.username} is already taken.")
        }

        // Check if new email is already taken by someone else
        if (user.email != userInformationUpdate.email &&
            userRepository.existsByEmail(userInformationUpdate.email)) {
            throw UserAlreadyExistsException("Email ${userInformationUpdate.email} is already registered.")
        }

        user.username = userInformationUpdate.username
        user.email = userInformationUpdate.email

        return user.toUserInformation()
    }

    @Transactional
    fun updateUserRole(id: Long, userRoleUpdate: UserRoleUpdate): UserInformation {
        val user: User = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }

        user.role = userRoleUpdate.role

        refreshTokenService.revokeAllUserSessions(user.id)

        return user.toUserInformation()
    }

    @Transactional
    fun updateUserPassword(id: Long, userPasswordUpdate: UserPasswordUpdate) {
        val user: User = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }

        if (!passwordEncoder.matches(userPasswordUpdate.currentPassword, user.password)) {
            throw InvalidPasswordException("Current password is incorrect.")
        }

        // 2. Prevent setting the exact same password (Optional Best Practice)
        if (passwordEncoder.matches(userPasswordUpdate.password, user.password)) {
            throw InvalidPasswordException("New password cannot be the same as current password.")
        }

        user.password = passwordEncoder.encodeNonNull(userPasswordUpdate.password)

        refreshTokenService.revokeAllUserSessions(userId = user.id)
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User not found with id: $id")
        }

        userRepository.deleteById(id)
    }
}
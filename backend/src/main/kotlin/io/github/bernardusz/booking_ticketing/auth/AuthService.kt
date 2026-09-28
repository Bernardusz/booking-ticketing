package io.github.bernardusz.booking_ticketing.auth

import io.github.bernardusz.booking_ticketing.auth.dto.LoginRequest
import io.github.bernardusz.booking_ticketing.auth.dto.LoginResponse
import io.github.bernardusz.booking_ticketing.auth.dto.RegisterRequest
import io.github.bernardusz.booking_ticketing.auth.service.JwtService
import io.github.bernardusz.booking_ticketing.auth.service.RefreshTokenService
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.InternalServerException
import io.github.bernardusz.booking_ticketing.shared.exception.exceptions.UserAlreadyExistsException
import io.github.bernardusz.booking_ticketing.user.User
import io.github.bernardusz.booking_ticketing.user.dto.UserCreation
import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import io.github.bernardusz.booking_ticketing.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService
) {
    @Transactional
    fun registerUser(registerRequest: RegisterRequest): Long {
        if (userRepository.existsByUsername(registerRequest.username)) {
            throw UserAlreadyExistsException("Username '${registerRequest.username}' is already taken.")
        }
        if (userRepository.existsByEmail(registerRequest.email)) {
            throw UserAlreadyExistsException("Email '${registerRequest.email}' is already registered.")
        }

        val hashedPassword = passwordEncoder.encode(registerRequest.password)
            ?: throw InternalServerException("Failed to register user - Password Hashing Error.")

        val newUser = User(
            username = registerRequest.username,
            email = registerRequest.email,
            password = hashedPassword,
        )

        return userRepository.save(newUser).id
    }

    @Transactional
    fun loginUser(loginRequest: LoginRequest): LoginResponse{
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password
            )
        )

        val user: User = userRepository.findByIdentifierSecurity(loginRequest.username)
            ?: throw UsernameNotFoundException("Username doesn't exist")

        val newAccessToken = jwtService.generateToken(UserSecurity(user))
        val refreshToken: String = refreshTokenService.createRefreshToken(user.id)

        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken,
            expiresIn = jwtService.getAccessTokenExpiration(),
            refreshTokenExpiresIn = refreshTokenService.getRefreshTokenExpiration()
        )
    }

    @Transactional
    fun refreshAccessToken(refreshToken: String): LoginResponse{
        val refreshTokenObject: RefreshToken =
            refreshTokenService.findValidToken(
            refreshToken
        )

        val user: User = refreshTokenObject.user

        val newAccessToken = jwtService.generateToken(UserSecurity(user))
        val newRefreshToken: String = refreshTokenService.createRefreshToken(user.id)
        refreshTokenService.revokeToken(refreshToken)

        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            expiresIn = jwtService.getAccessTokenExpiration(),
            refreshTokenExpiresIn = refreshTokenService.getRefreshTokenExpiration()
        )
    }

    @Transactional
    fun findValidToken(rawToken: String): RefreshToken?{
        val refreshToken: RefreshToken =
            refreshTokenService.findValidToken(
                rawToken
            )
        return refreshToken
    }

    @Transactional
    fun getUserTokens(userId: Long): List<RefreshToken> {
        return refreshTokenService.getUserTokens(userId)
    }

    @Transactional
    fun revokeAllUserSessions(userId: Long): Int {
        return refreshTokenService.revokeAllUserSessions(userId)
    }
}
package io.github.bernardusz.booking_ticketing.auth

import io.github.bernardusz.booking_ticketing.auth.dto.LoginRequest
import io.github.bernardusz.booking_ticketing.auth.dto.LoginResponse
import io.github.bernardusz.booking_ticketing.auth.dto.RegisterRequest
import io.github.bernardusz.booking_ticketing.auth.dto.UserSessionResponse
import io.github.bernardusz.booking_ticketing.auth.service.RefreshTokenService
import io.github.bernardusz.booking_ticketing.user.dto.UserCreation
import io.github.bernardusz.booking_ticketing.user.dto.UserInformation
import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import io.github.bernardusz.booking_ticketing.shared.util.createAccessTokenCookie
import io.github.bernardusz.booking_ticketing.shared.util.createRefreshTokenCookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService,
) {
    @GetMapping("/me")
    fun getUser(
        @AuthenticationPrincipal user: UserSecurity?
    ): ResponseEntity<UserSessionResponse> {
        if (user == null){
            return ResponseEntity.ok(UserSessionResponse(isAuthenticated = false))
        }

        val userInfo = UserInformation(
            id = user.getId(),
            username = user.username,
            email = user.getEmail(),
            role = user.getRole(),
        )

        return ResponseEntity.ok(
            UserSessionResponse(
                isAuthenticated = true,
                user = userInfo,
            )
        )
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<Void> {
        val loginResponse: LoginResponse = authService.loginUser(loginRequest)

        val accessTokenCookie: ResponseCookie =
            createAccessTokenCookie(loginResponse.accessToken, loginResponse.expiresIn)

        val refreshTokenCookie: ResponseCookie =
            createRefreshTokenCookie(loginResponse.refreshToken, loginResponse.refreshTokenExpiresIn)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString() )
            .build()
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(
        request: HttpServletRequest,
    ): ResponseEntity<Void> {
        val refreshToken: String? = request.cookies
            ?.firstOrNull { it.name == "REFRESH-TOKEN" }
            ?.value

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val loginResponse: LoginResponse = authService.refreshAccessToken(refreshToken)

        val accessTokenCookie: ResponseCookie =
            createAccessTokenCookie(loginResponse.accessToken, loginResponse.expiresIn)

        val refreshTokenCookie: ResponseCookie =
            createRefreshTokenCookie(loginResponse.refreshToken, loginResponse.refreshTokenExpiresIn)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString() )
            .build()
    }

    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest
    ): ResponseEntity<Void> {
        val refreshToken: String? = request.cookies
            ?.firstOrNull { it.name == "REFRESH-TOKEN" }
            ?.value

        if (refreshToken != null) {
            refreshTokenService.revokeToken(refreshToken)
        }

        val accessTokenCookie: ResponseCookie =
            createAccessTokenCookie("", 0)

        val refreshTokenCookie: ResponseCookie =
            createRefreshTokenCookie("", 0)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString() )
            .build()
    }

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest,
    ): ResponseEntity<Void> {
        val userId: Long = authService.registerUser(registerRequest)
        return ResponseEntity.created(
            URI.create("/api/v1/users/$userId")
        ).build()
    }
}
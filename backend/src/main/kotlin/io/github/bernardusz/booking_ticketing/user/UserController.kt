package io.github.bernardusz.booking_ticketing.user

import io.github.bernardusz.booking_ticketing.auth.dto.RegisterRequest
import io.github.bernardusz.booking_ticketing.user.dto.UserInformation
import io.github.bernardusz.booking_ticketing.user.dto.UserInformationUpdate
import io.github.bernardusz.booking_ticketing.user.dto.UserPasswordUpdate
import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import io.github.bernardusz.booking_ticketing.shared.util.createAccessTokenCookie
import io.github.bernardusz.booking_ticketing.shared.util.createRefreshTokenCookie
import io.github.bernardusz.booking_ticketing.user.dto.UserCreation
import io.github.bernardusz.booking_ticketing.user.dto.UserRoleUpdate
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    // --- ADMIN ENDPOINTS ---

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createUser(
        @Valid @RequestBody userCreation: UserCreation,
    ): ResponseEntity<Void> {
        val userId: Long = userService.registerUser(userCreation)
        return ResponseEntity.created(
            URI.create("/api/v1/users/$userId")
        ).build()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun searchUsers(
        @RequestParam(required = false) search: String?
    ): ResponseEntity<List<UserInformation>> {
        val results = if (search.isNullOrBlank()) {
            userService.getUsers()
        } else {
            userService.searchUserByUsernameAndEmail(search)
        }
        return ResponseEntity.ok(results)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long):
            ResponseEntity<UserInformation> {
        return ResponseEntity.ok(
            userService.getUserById(id)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String):
            ResponseEntity<UserInformation> {
        return ResponseEntity.ok(
            userService.getUserByEmail(email)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String):
            ResponseEntity<UserInformation> {
        return ResponseEntity.ok(
            userService.getUserByUsername(username)
        )
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ticket/{ticketId}")
    fun getUserByTicketId(
        @PathVariable ticketId: Long
    ): ResponseEntity<UserInformation> {
        return ResponseEntity.ok(
            userService.getUserByTicketId(ticketId)
        )
    }

    // --- SELF & ADMIN SHARED ENDPOINTS ---

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/information")
    fun updateUserInformation(
        @AuthenticationPrincipal user: UserSecurity,
        @Valid @RequestBody userInformationUpdate: UserInformationUpdate
    ): ResponseEntity<UserInformation> {
        val updatedUser = userService.updateUserInformation(
            id = user.getId(),
            userInformationUpdate = userInformationUpdate
        )
        return ResponseEntity.ok(updatedUser)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    fun updateUserRoleById(
        @PathVariable id: Long,
        @Valid @RequestBody userRoleUpdate: UserRoleUpdate
    ): ResponseEntity<UserInformation> {
        val updatedUser = userService.updateUserRole(
            id = id,
            userRoleUpdate = userRoleUpdate
        )
        return ResponseEntity.ok(updatedUser)
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password")
    fun updateUserPassword(
        @AuthenticationPrincipal user: UserSecurity,
        @Valid @RequestBody userPasswordUpdate: UserPasswordUpdate
    ): ResponseEntity<Void> {
        userService.updateUserPassword(
            id = user.getId(),
            userPasswordUpdate = userPasswordUpdate
        )

        val accessTokenCookie: ResponseCookie =
            createAccessTokenCookie("", 0)

        val refreshTokenCookie: ResponseCookie =
            createRefreshTokenCookie("", 0)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString() )
            .build()
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @DeleteMapping("/{id}")
    fun deleteUserById(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: UserSecurity
    ): ResponseEntity<Void> {
        userService.deleteUser(id)

        if (id == user.getId()) {
            val accessTokenCookie = createAccessTokenCookie("", 0)
            val refreshTokenCookie = createRefreshTokenCookie("", 0)

            return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build()
        }

        return ResponseEntity.noContent().build()
    }
}
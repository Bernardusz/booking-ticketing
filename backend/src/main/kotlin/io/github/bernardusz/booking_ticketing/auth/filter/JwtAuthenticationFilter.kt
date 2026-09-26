package io.github.bernardusz.booking_ticketing.auth.filter

import io.github.bernardusz.booking_ticketing.auth.service.CustomUserDetailsService
import io.github.bernardusz.booking_ticketing.auth.service.JwtService
import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtAuthenticationFilter(
    @Qualifier("handlerExceptionResolver") private val exceptionResolver: HandlerExceptionResolver,
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path == "/api/v1/auth/login"
                || path == "/api/v1/auth/register"
                || path == "/api/v1/auth/refresh"
                || path == "/api/v1/auth/logout"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt: String? = request.cookies
            ?.firstOrNull { it.name == "AUTH-TOKEN" }
            ?.value

        if (jwt == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val userId: String = jwtService.extractSubjectId(jwt)
            val authentication: Authentication? = SecurityContextHolder.getContext().authentication

            if (userId != null && authentication == null) {
                val userSecurity: UserSecurity = customUserDetailsService.loadUserById(userId)
                if (jwtService.validateToken(jwt, userSecurity)) {
                    val authToken: UsernamePasswordAuthenticationToken =
                        UsernamePasswordAuthenticationToken(
                            userSecurity,
                            null,
                            userSecurity.authorities
                        )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }
        catch (e: Exception) {
            exceptionResolver.resolveException(request, response, null, e)
            return
        }
        filterChain.doFilter(request, response)
    }
}
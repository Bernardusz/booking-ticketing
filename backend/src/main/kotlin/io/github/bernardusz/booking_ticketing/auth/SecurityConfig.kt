package io.github.bernardusz.booking_ticketing.auth

import io.github.bernardusz.booking_ticketing.auth.filter.JwtAuthenticationFilter
import io.github.bernardusz.booking_ticketing.auth.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customUserDetailsService: CustomUserDetailsService
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/logout",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/me",
                    "/error"
                ).permitAll()
                .requestMatchers(
                    HttpMethod.GET, "/api/v1/movies/**"
                ).permitAll()
                .anyRequest().authenticated()
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://localhost:5173",
            "https://localhost:5173",
        )
        configuration.allowedMethods = listOf(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        )
        configuration.allowedHeaders = listOf(
            "Authorization",
            "Content-Type",
            "Cookie"
        )
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider(
            customUserDetailsService
        ).apply {
            setPasswordEncoder(passwordEncoder())
        }
        return provider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        config: AuthenticationConfiguration
    ): AuthenticationManager {
        return config.getAuthenticationManager()
    }

}
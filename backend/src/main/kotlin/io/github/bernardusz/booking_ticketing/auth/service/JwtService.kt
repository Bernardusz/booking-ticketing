package io.github.bernardusz.booking_ticketing.auth.service

import io.github.bernardusz.booking_ticketing.user.dto.UserSecurity
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService (
    @Value("\${jwt.secret}") private val secret: String,

    @Value("\${jwt.access_token.expiration}")
    private val accessTokenExpiration: Long,
){
    fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    fun buildToken(
        extraClaims: Map<String, Any>,
        user: UserSecurity
    ): String {
        return Jwts.builder()
            .subject(user.getId().toString())
            .claims(extraClaims)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(getSigningKey(), Jwts.SIG.HS512)
            .compact()
    }

    fun generateToken(user: UserSecurity): String {
        return buildToken(mapOf(), user)
    }

    fun extractSubjectId(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun validateToken(token: String, user: UserSecurity): Boolean {
        val extractedUserId = extractSubjectId(token)

        return extractedUserId == user.getId().toString() && !isTokenExpired(token)
    }

    fun getAccessTokenExpiration(): Long {
        return accessTokenExpiration
    }
}
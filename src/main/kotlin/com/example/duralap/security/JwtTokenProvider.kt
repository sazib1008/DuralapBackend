package com.example.duralap.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider {

    @Value("\${app.jwt.secret:mySecretKey}")
    private lateinit var jwtSecret: String

    @Value("\${app.jwt.expiration-in-ms:86400000}") // 24 hours default
    private var jwtExpirationInMs: Long = 86400000

    private val secretKey: Key by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    /**
     * Generate JWT token
     */
    fun generateToken(username: String): String {
        val claims = mutableMapOf<String, Any>()
        claims["sub"] = username
        claims["created"] = Date()

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationInMs))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * Get username from JWT token
     */
    fun getUsernameFromToken(token: String): String {
        val claims = getClaimsFromToken(token)
        return claims.subject
    }

    /**
     * Validate JWT token
     */
    fun validateToken(token: String): Boolean {
        return try {
            getClaimsFromToken(token)
            !isTokenExpired(token)
        } catch (ex: JwtException) {
            false
        }
    }

    /**
     * Check if token is expired
     */
    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    /**
     * Get expiration date from JWT token
     */
    private fun getExpirationDateFromToken(token: String): Date {
        val claims = getClaimsFromToken(token)
        return claims.expiration
    }

    /**
     * Get claims from JWT token
     */
    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
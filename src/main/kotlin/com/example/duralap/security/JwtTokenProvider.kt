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

    @Value("\${app.jwt.expiration-in-ms:86400000}") // 24 hours default for access token
    private var jwtExpirationInMs: Long = 86400000

    @Value("\${app.jwt.refresh-expiration-in-ms:604800000}") // 7 days default for refresh token
    private var jwtRefreshExpirationInMs: Long = 604800000

    private val secretKey: Key by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    /**
     * Generate Access Token
     */
    fun generateAccessToken(username: String, roles: Set<String>): String {
        val claims = mutableMapOf<String, Any>()
        claims["sub"] = username
        claims["created"] = Date()
        claims["roles"] = roles.joinToString(",")

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationInMs))
            .claim("token_type", "access_token")
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * Generate Refresh Token
     */
    fun generateRefreshToken(username: String): String {
        val claims = mutableMapOf<String, Any>()
        claims["sub"] = username
        claims["created"] = Date()

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtRefreshExpirationInMs))
            .claim("token_type", "refresh_token")
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * Get username from JWT token
     */
    fun getUsernameFromToken(token: String): String {
        val claims = getClaimsFromToken(token)
        return claims.subject ?: throw IllegalArgumentException("Token subject is null")
    }

    /**
     * Get roles from JWT token
     */
    fun getRolesFromToken(token: String): Set<String> {
        val claims = getClaimsFromToken(token)
        val rolesString = claims["roles"] as? String ?: return emptySet()
        return rolesString.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
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
     * Check if token is an access token
     */
    fun isAccessToken(token: String?): Boolean {
        return token != null && try {
            val claims = getClaimsFromToken(token)
            "access_token" == claims["token_type"]
        } catch (ex: JwtException) {
            false
        }
    }

    /**
     * Check if token is a refresh token
     */
    fun isRefreshToken(token: String?): Boolean {
        return token != null && try {
            val claims = getClaimsFromToken(token)
            "refresh_token" == claims["token_type"]
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
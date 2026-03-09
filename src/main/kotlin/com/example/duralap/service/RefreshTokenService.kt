package com.example.duralap.service

import com.example.duralap.database.model.RefreshToken
import com.example.duralap.database.model.User
import com.example.duralap.database.repository.RefreshTokenRepository
import com.example.duralap.database.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {

    @Value("\${app.jwt.refresh-expiration-in-ms:604800000}") // 7 days default
    private var refreshTokenExpirationInMs: Long = 604800000

    fun createRefreshToken(user: User): RefreshToken {
        // Revoke all existing refresh tokens for this user (optional security measure)
        revokeAllTokensForUser(user.id!!)

        val token = UUID.randomUUID().toString()
        val expiryDate = Instant.now().plusMillis(refreshTokenExpirationInMs)

        val refreshToken = RefreshToken(
            userId = user.id!!,
            token = token,
            expiryDate = expiryDate
        )

        return refreshTokenRepository.save(refreshToken)
    }

    fun verifyExpiration(refreshToken: RefreshToken): RefreshToken {
        if (refreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            throw RuntimeException("Refresh token expired")
        }
        return refreshToken
    }

    fun findByToken(token: String): Optional<RefreshToken> {
        return refreshTokenRepository.findByTokenAndRevokedIsFalseAndBlacklistedIsFalse(token)
    }

    fun findByUserId(userId: String): List<RefreshToken> {
        return refreshTokenRepository.findByUserId(userId)
    }

    fun revokeToken(token: String) {
        val refreshToken = refreshTokenRepository.findByToken(token)
        if (refreshToken.isPresent) {
            val tokenToUpdate = refreshToken.get()
            val updatedToken = tokenToUpdate.copy(revoked = true)
            refreshTokenRepository.save(updatedToken)
        }
    }

    fun revokeAllTokensForUser(userId: String) {
        val tokens = refreshTokenRepository.findByUserId(userId)
        tokens.forEach { token ->
            val updatedToken = token.copy(revoked = true)
            refreshTokenRepository.save(updatedToken)
        }
    }

    fun deleteToken(token: String) {
        refreshTokenRepository.deleteByToken(token)
    }

    fun deleteTokensForUser(userId: String) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}

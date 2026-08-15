package com.example.duralap.auth.application.service

import com.example.duralap.auth.domain.model.RefreshToken
import com.example.duralap.auth.domain.repository.RefreshTokenRepository
import com.example.duralap.exception.TokenRefreshException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @Value("\${app.jwt.refresh-expiration-in-ms:604800000}")
    private var refreshTokenExpirationInMs: Long = 604800000

    fun createRefreshToken(userId: String): RefreshToken {
        revokeAllTokensForUser(userId)

        val token = UUID.randomUUID().toString()
        val expiryDate = Instant.now().plusMillis(refreshTokenExpirationInMs)

        val refreshToken = RefreshToken(
            userId = userId,
            token = token,
            expiryDate = expiryDate
        )

        return refreshTokenRepository.save(refreshToken)
    }

    fun verifyExpiration(refreshToken: RefreshToken): RefreshToken {
        if (refreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            throw TokenRefreshException("Refresh token expired")
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

package com.example.duralap.auth.domain.repository

import com.example.duralap.auth.domain.model.RefreshToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RefreshTokenRepository : MongoRepository<RefreshToken, String> {

    fun findByToken(token: String): Optional<RefreshToken>

    fun findByTokenAndRevokedIsFalseAndBlacklistedIsFalse(token: String): Optional<RefreshToken>

    fun findByUserId(userId: String): List<RefreshToken>

    fun deleteByToken(token: String): Long

    fun deleteByUserId(userId: String): Long
}

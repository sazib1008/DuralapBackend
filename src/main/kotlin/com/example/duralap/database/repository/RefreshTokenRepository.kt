package com.example.duralap.database.repository

import com.example.duralap.database.model.RefreshToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RefreshTokenRepository : MongoRepository<RefreshToken, String> {
    fun findByToken(token: String): Optional<RefreshToken>
    fun findByUserId(userId: String): List<RefreshToken>
    fun deleteByUserId(userId: String)
    fun deleteByToken(token: String)
    fun findByTokenAndRevokedIsFalseAndBlacklistedIsFalse(token: String): Optional<RefreshToken>
}

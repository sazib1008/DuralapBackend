package com.example.duralap.auth.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("refresh_tokens")
data class RefreshToken(
    @Id
    val id: String? = null,

    @Indexed
    val userId: String,

    @Indexed(unique = true)
    val token: String,

    val expiryDate: Instant,

    val revoked: Boolean = false,

    val blacklisted: Boolean = false,

    val createdAt: Instant = Instant.now()
)

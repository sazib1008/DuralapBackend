package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "refresh_tokens")
data class RefreshToken(
    @Id
    val id: String? = null,

    @Indexed
    val userId: String,

    @Indexed
    val token: String,

    val expiryDate: Instant,

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),

    val revoked: Boolean = false,
    val blacklisted: Boolean = false
)

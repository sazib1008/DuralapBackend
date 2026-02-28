package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("conversations")
data class Conversation(
    @Id val id: String? = null,
    val participantIds: Set<String>,
    val createdAt: Instant = Instant.now()
)
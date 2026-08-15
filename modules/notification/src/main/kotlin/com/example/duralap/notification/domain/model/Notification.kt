package com.example.duralap.notification.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("notifications")
@CompoundIndexes(
    CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}"),
    CompoundIndex(name = "user_status_idx", def = "{'userId': 1, 'status': 1}")
)
data class Notification(
    @Id
    val id: String? = null,

    @Indexed
    val userId: String,

    val type: String,

    val title: String,

    val body: String,

    val data: Map<String, String> = emptyMap(),

    val status: String = "CREATED",

    val readAt: Instant? = null,

    val createdAt: Instant = Instant.now()
)

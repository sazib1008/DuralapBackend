package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "notifications")
data class Notification(
    @Id
    val id: String? = null,
    val userId: String,
    val type: String,                 // NEW_MESSAGE, MESSAGE_REACTION, MESSAGE_READ, GROUP_INVITE, GROUP_MEMBER_ADDED, SYSTEM
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap(),
    val status: String = "CREATED",   // CREATED, DELIVERED, READ, EXPIRED, DELETED
    val createdAt: Instant = Instant.now(),
    val readAt: Instant? = null
)

package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("messages")
data class Message(
    @Id val id: String? = null,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null, // For images, videos, files
    val mediaType: String? = null, // image/jpeg, video/mp4, etc.
    val fileName: String? = null, // For file messages
    val fileSize: Long? = null, // File size in bytes
    val isRead: Boolean = false,
    val readAt: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    LOCATION,
    CONTACT
}
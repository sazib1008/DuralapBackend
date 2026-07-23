package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("conversations")
data class Conversation(
    @Id val id: String? = null,
    val participantIds: Set<String>,
    val status: ConversationStatus = ConversationStatus.ACCEPTED, // Default to ACCEPTED for backward compatibility
    val createdAt: Instant = Instant.now(),
    var lastMessageId: String? = null,
    var lastMessageSenderId: String? = null,
    var lastMessageContent: String? = null,
    var lastMessageType: com.example.duralap.database.model.MessageType? = null,
    var lastMessageAt: Instant? = null,
    var archivedBy: Set<String> = emptySet() // Users who archived this conversation
)
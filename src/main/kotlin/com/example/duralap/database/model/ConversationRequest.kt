package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Tracks conversation requests between users.
 * When User A messages User B for the first time, a ConversationRequest is created.
 * User B can then accept or reject this request.
 */
@Document("conversation_requests")
@CompoundIndex(name = "idx_sender_recipient_status", def = "{'senderId': 1, 'recipientId': 1, 'status': 1}")
data class ConversationRequest(
    @Id
    val id: String? = null,

    @Indexed
    val senderId: String,              // User who initiated the conversation

    @Indexed
    val recipientId: String,           // User who received the request

    val conversationId: String,        // Associated conversation ID

    val status: ConversationStatus = ConversationStatus.PENDING,

    val initialMessage: String? = null, // Optional first message from sender

    val requestedAt: Instant = Instant.now(),

    val respondedAt: Instant? = null,   // When recipient responded

    val respondedBy: String? = null     // Who responded (recipientId)
)

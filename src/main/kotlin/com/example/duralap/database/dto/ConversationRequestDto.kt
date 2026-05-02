package com.example.duralap.database.dto

import com.example.duralap.database.model.ConversationStatus
import java.time.Instant

data class ConversationRequestResponse(
    val id: String,
    val senderId: String,
    val senderUsername: String? = null,
    val senderFullName: String? = null,
    val senderProfileImageUrl: String? = null,
    val recipientId: String,
    val conversationId: String,
    val status: ConversationStatus,
    val initialMessage: String? = null,
    val requestedAt: Instant,
    val respondedAt: Instant? = null
)

data class ConversationActionRequest(
    val conversationRequestId: String
)

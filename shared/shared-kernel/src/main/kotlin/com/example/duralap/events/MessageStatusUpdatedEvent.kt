package com.example.duralap.events

import com.example.duralap.database.model.MessageStatus
import java.time.Instant

data class MessageStatusUpdatedEvent(
    val messageId: String,
    val conversationId: String,
    val senderId: String,
    val status: MessageStatus,
    val timestamp: Instant = Instant.now()
)

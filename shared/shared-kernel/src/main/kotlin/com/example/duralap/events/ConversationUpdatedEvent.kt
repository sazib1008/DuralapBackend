package com.example.duralap.events

import com.example.duralap.database.model.MessageType
import java.time.Instant

data class ConversationUpdatedEvent(
    val conversationId: String,
    val lastMessageId: String? = null,
    val lastMessageSenderId: String? = null,
    val lastMessageContent: String,
    val lastMessageType: MessageType = MessageType.TEXT,
    val lastMessageAt: Instant,
    val participantIds: Set<String> = emptySet()
)


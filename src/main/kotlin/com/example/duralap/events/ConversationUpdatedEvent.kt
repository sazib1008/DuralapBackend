package com.example.duralap.events

import java.time.Instant

data class ConversationUpdatedEvent(
    val conversationId: String,
    val lastMessageContent: String,
    val lastMessageAt: Instant,
    val participantIds: Set<String>
)

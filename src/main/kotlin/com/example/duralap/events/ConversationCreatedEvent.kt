package com.example.duralap.events

import java.time.Instant

data class ConversationCreatedEvent(
    val id: String,
    val participantIds: Set<String>,
    val createdAt: Instant
)

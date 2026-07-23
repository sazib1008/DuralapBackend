package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("user_conversations")
data class UserConversations(
    @Id
    val userId: String,
    val conversationIds: Set<String> = emptySet()
)

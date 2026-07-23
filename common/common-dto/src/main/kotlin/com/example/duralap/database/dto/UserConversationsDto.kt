package com.example.duralap.database.dto

data class UserConversationsDto(
    val userId: String,
    val conversationIds: Set<String>
)

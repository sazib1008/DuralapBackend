package com.example.duralap.database.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class ConversationCreateRequest(
    @field:NotEmpty(message = "Participant IDs cannot be empty")
    val participantIds: Set<String>
)

data class ConversationResponse(
    val id: String,
    val participantIds: Set<String>,
    val createdAt: java.time.Instant,
    val lastMessage: MessageResponse? = null,
    val unreadCount: Int = 0
)

data class GetOrCreateConversationRequest(
    @field:NotBlank(message = "User1 ID is required")
    val user1Id: String,
    
    @field:NotBlank(message = "User2 ID is required")
    val user2Id: String
)
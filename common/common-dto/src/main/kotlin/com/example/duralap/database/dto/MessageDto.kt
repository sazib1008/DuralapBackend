package com.example.duralap.database.dto

import com.example.duralap.database.model.MessageType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class MessageCreateRequest(
    @field:NotBlank(message = "Conversation ID is required")
    val conversationId: String,
    
    @field:NotBlank(message = "Sender ID is required")
    val senderId: String,
    
    @field:NotBlank(message = "Content is required")
    val content: String,
    
    @field:NotNull(message = "Message type is required")
    val messageType: MessageType = MessageType.TEXT,
    
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null
)

data class MessageResponse(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: MessageType,
    val mediaUrl: String?,
    val mediaType: String?,
    val fileName: String?,
    val fileSize: Long?,
    val isRead: Boolean,
    val readAt: java.time.Instant?,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant,
    val senderInfo: UserInfo? = null
)

data class UserInfo(
    val id: String,
    val username: String,
    val fullName: String?,
    val profileImageUrl: String?
)

data class MessageReadRequest(
    @field:NotBlank(message = "Message ID is required")
    val messageId: String,
    
    @field:NotBlank(message = "User ID is required")
    val userId: String
)
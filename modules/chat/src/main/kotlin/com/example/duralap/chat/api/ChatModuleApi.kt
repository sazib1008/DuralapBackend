package com.example.duralap.chat.api

import com.example.duralap.database.dto.ConversationResponse
import com.example.duralap.database.model.MessageType
import java.time.Instant

interface ChatModuleApi {
    fun getConversationById(id: String): ConversationResponse?
    fun isUserParticipant(conversationId: String, userId: String): Boolean
    fun getParticipantIds(conversationId: String): Set<String>
    fun isConversationAccepted(conversationId: String): Boolean
    fun updateLastMessage(
        conversationId: String,
        messageId: String,
        senderId: String,
        content: String,
        messageType: MessageType,
        timestamp: Instant
    )
    fun getUserConversationIds(userId: String): Set<String>
    fun updateUserConversations(userId: String, conversationId: String, add: Boolean = true)
}

package com.example.duralap.message.api

import com.example.duralap.database.dto.MessageCreateRequest
import com.example.duralap.database.dto.MessageResponse
import com.example.duralap.database.model.MessageType

interface MessageModuleApi {
    fun sendMessage(request: MessageCreateRequest): MessageResponse
    fun getMessageById(id: String): MessageResponse?
    fun getLastMessage(conversationId: String): MessageResponse?
    fun countUnreadMessages(conversationId: String, userId: String): Long
    fun deleteMessagesByConversationId(conversationId: String): Long
    fun getMessagesByType(conversationId: String, messageType: MessageType): List<MessageResponse>
    fun getMediaMessages(conversationId: String): List<MessageResponse>
}

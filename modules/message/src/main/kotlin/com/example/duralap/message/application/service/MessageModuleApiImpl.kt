package com.example.duralap.message.application.service

import com.example.duralap.database.dto.MessageCreateRequest
import com.example.duralap.database.dto.MessageResponse
import com.example.duralap.database.model.MessageType
import com.example.duralap.message.api.MessageModuleApi
import com.example.duralap.message.domain.repository.MessageRepository
import org.springframework.stereotype.Service

@Service
class MessageModuleApiImpl(
    private val messageService: MessageService,
    private val messageRepository: MessageRepository
) : MessageModuleApi {

    override fun sendMessage(request: MessageCreateRequest): MessageResponse {
        return messageService.sendMessage(request)
    }

    override fun getMessageById(id: String): MessageResponse? {
        return messageService.getMessageById(id)
    }

    override fun getLastMessage(conversationId: String): MessageResponse? {
        return messageService.getLastMessage(conversationId)
    }

    override fun countUnreadMessages(conversationId: String, userId: String): Long {
        return messageService.getUnreadMessagesCount(conversationId, userId)
    }

    override fun deleteMessagesByConversationId(conversationId: String): Long {
        return messageRepository.deleteByConversationId(conversationId)
    }

    override fun getMessagesByType(conversationId: String, messageType: MessageType): List<MessageResponse> {
        return messageService.getMessagesByType(conversationId, messageType)
    }

    override fun getMediaMessages(conversationId: String): List<MessageResponse> {
        return messageService.getMediaMessages(conversationId)
    }
}

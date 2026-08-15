package com.example.duralap.chat.application.service

import com.example.duralap.chat.api.ChatModuleApi
import com.example.duralap.chat.domain.model.Conversation
import com.example.duralap.chat.domain.model.toConversationResponse
import com.example.duralap.chat.domain.repository.ConversationRepository
import com.example.duralap.database.dto.ConversationResponse
import com.example.duralap.database.model.ConversationStatus
import com.example.duralap.database.model.MessageType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatModuleApiImpl(
    private val conversationRepository: ConversationRepository,
    private val conversationService: ConversationService
) : ChatModuleApi {

    override fun getConversationById(id: String): ConversationResponse? {
        return conversationService.getConversationById(id)
    }

    override fun isUserParticipant(conversationId: String, userId: String): Boolean {
        return conversationService.isUserParticipant(conversationId, userId)
    }

    override fun getParticipantIds(conversationId: String): Set<String> {
        val conversation = conversationRepository.findByIdOrNull(conversationId) ?: return emptySet()
        return conversation.participantIds
    }

    override fun isConversationAccepted(conversationId: String): Boolean {
        val conversation = conversationRepository.findByIdOrNull(conversationId) ?: return false
        return conversation.status == ConversationStatus.ACCEPTED
    }

    override fun updateLastMessage(
        conversationId: String,
        messageId: String,
        senderId: String,
        content: String,
        messageType: MessageType,
        timestamp: Instant
    ) {
        conversationRepository.findByIdOrNull(conversationId)?.let { conv ->
            conv.lastMessageId = messageId
            conv.lastMessageSenderId = senderId
            conv.lastMessageContent = content
            conv.lastMessageType = messageType
            conv.lastMessageAt = timestamp
            conversationRepository.save(conv)
        }
    }

    override fun getUserConversationIds(userId: String): Set<String> {
        return conversationService.getUserConversationIds(userId)
    }

    override fun updateUserConversations(userId: String, conversationId: String, add: Boolean) {
        conversationService.updateUserConversations(userId, conversationId, add)
    }
}

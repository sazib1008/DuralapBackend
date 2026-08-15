package com.example.duralap.message.application.service

import com.example.duralap.chat.api.ChatModuleApi
import com.example.duralap.config.RateLimitingService
import com.example.duralap.database.dto.*
import com.example.duralap.database.model.*
import com.example.duralap.events.MessageCreatedEvent
import com.example.duralap.events.MessageStatusUpdatedEvent
import com.example.duralap.message.application.cache.ConversationValidationCache
import com.example.duralap.message.domain.model.Message
import com.example.duralap.message.domain.model.toMessageResponse
import com.example.duralap.message.domain.repository.MessageRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class MessageService(
    private val eventPublisher: ApplicationEventPublisher,
    private val conversationValidator: ConversationValidationCache,
    private val messageRepository: MessageRepository,
    private val chatModuleApi: ChatModuleApi,
    private val rateLimitingService: RateLimitingService
) {

    fun sendMessage(request: MessageCreateRequest): MessageResponse {
        if (!rateLimitingService.canSendMessage(request.senderId)) {
            throw IllegalArgumentException("Rate limit exceeded. Please wait before sending more messages.")
        }

        val clientUuid = request.clientMsgId
        if (!clientUuid.isNullOrBlank()) {
            val existing = messageRepository.findByClientMsgId(clientUuid)
            if (existing != null) {
                return existing.toMessageResponse()
            }
        }
        
        val transactionId = UUID.randomUUID().toString()
        val timestamp = Instant.now()
        
        if (!chatModuleApi.isConversationAccepted(request.conversationId)) {
            throw IllegalArgumentException("Cannot send message. Conversation request is still pending or was rejected.")
        }
        
        conversationValidator.verifyUserParticipantInConversation(
            userId = request.senderId, 
            conversationId = request.conversationId
        )

        val message = Message(
            id = transactionId,
            clientMsgId = request.clientMsgId,
            conversationId = request.conversationId,
            senderId = request.senderId,
            content = request.content,
            messageType = request.messageType,
            mediaUrl = request.mediaUrl,
            mediaType = request.mediaType,
            fileName = request.fileName,
            fileSize = request.fileSize,
            status = MessageStatus.SENT,
            isRead = false,
            createdAt = timestamp,
            updatedAt = timestamp
        )
        val savedMessage = messageRepository.save(message)

        chatModuleApi.updateLastMessage(
            conversationId = request.conversationId,
            messageId = savedMessage.id!!,
            senderId = savedMessage.senderId,
            content = savedMessage.content,
            messageType = savedMessage.messageType,
            timestamp = timestamp
        )

        val event = MessageCreatedEvent(
            id = transactionId,
            clientMsgId = request.clientMsgId,
            conversationId = request.conversationId,
            senderId = request.senderId,
            content = request.content,
            messageType = request.messageType,
            mediaUrl = request.mediaUrl,
            mediaType = request.mediaType,
            fileName = request.fileName,
            fileSize = request.fileSize,
            status = MessageStatus.SENT,
            timestamp = timestamp
        )
        eventPublisher.publishEvent(event)

        return savedMessage.toMessageResponse()
    }

    fun syncMessages(userId: String, since: Instant?): List<MessageResponse> {
        val convIds = chatModuleApi.getUserConversationIds(userId).toList()
        if (convIds.isEmpty()) return emptyList()

        val messages = if (since != null) {
            messageRepository.findByConversationIdInAndUpdatedAtAfterOrderByCreatedAtAsc(convIds, since)
        } else {
            messageRepository.findByConversationIdInAndCreatedAtAfterOrderByCreatedAtAsc(
                convIds,
                Instant.now().minus(java.time.Duration.ofDays(7))
            )
        }

        val updatedMessages = messages.map { msg ->
            if (msg.senderId != userId && msg.status == MessageStatus.SENT) {
                val deliveredMsg = msg.copy(
                    status = MessageStatus.DELIVERED,
                    updatedAt = Instant.now()
                )
                messageRepository.save(deliveredMsg)
            } else {
                msg
            }
        }

        return updatedMessages.map { it.toMessageResponse() }
    }

    fun updateMessageStatus(messageId: String, newStatus: MessageStatus, userId: String): MessageResponse {
        val message = messageRepository.findByIdOrNull(messageId)
            ?: throw IllegalArgumentException("Message not found")

        conversationValidator.verifyUserParticipantInConversation(userId, message.conversationId)

        val updatedMessage = message.copy(
            status = newStatus,
            isRead = if (newStatus == MessageStatus.READ) true else message.isRead,
            readAt = if (newStatus == MessageStatus.READ) (message.readAt ?: Instant.now()) else message.readAt,
            updatedAt = Instant.now()
        )

        val savedMessage = messageRepository.save(updatedMessage)

        val statusEvent = MessageStatusUpdatedEvent(
            messageId = savedMessage.id!!,
            conversationId = savedMessage.conversationId,
            senderId = savedMessage.senderId,
            status = savedMessage.status,
            timestamp = Instant.now()
        )
        eventPublisher.publishEvent(statusEvent)

        return savedMessage.toMessageResponse()
    }

    fun getMessages(conversationId: String, page: Int = 0, size: Int = 20): List<MessageResponse> {
        val pageable: Pageable = PageRequest.of(page, size)
        val messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable).content
        return messages.reversed().map { it.toMessageResponse() }
    }

    fun getAllMessages(conversationId: String): List<MessageResponse> {
        val messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId)
        return messages.reversed().map { it.toMessageResponse() }
    }

    fun getMessageById(id: String): MessageResponse? {
        return messageRepository.findByIdOrNull(id)?.toMessageResponse()
    }

    fun markMessageAsRead(messageId: String, userId: String): MessageResponse {
        return updateMessageStatus(messageId, MessageStatus.READ, userId)
    }

    fun markAllMessagesAsRead(conversationId: String, userId: String) {
        val messages = messageRepository.markMessagesAsRead(conversationId, userId)
        messages.forEach { message ->
            val updatedMessage = message.copy(
                status = MessageStatus.READ,
                isRead = true,
                readAt = Instant.now(),
                updatedAt = Instant.now()
            )
            messageRepository.save(updatedMessage)
        }
    }

    fun getUnreadMessagesCount(conversationId: String, userId: String): Long {
        return messageRepository.countUnreadMessages(conversationId, userId)
    }

    fun getUnreadMessages(conversationId: String, userId: String): List<MessageResponse> {
        val messages = messageRepository.findUnreadMessages(conversationId, userId)
        return messages.map { it.toMessageResponse() }
    }

    fun getLastMessage(conversationId: String): MessageResponse? {
        val message = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
        return message?.toMessageResponse()
    }

    fun deleteMessage(id: String, userId: String): Boolean {
        val message = messageRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Message not found")

        if (message.senderId != userId) {
            throw IllegalArgumentException("Only sender can delete the message")
        }

        messageRepository.deleteById(id)
        return true
    }

    fun getMessagesByType(conversationId: String, messageType: MessageType): List<MessageResponse> {
        return messageRepository.findByConversationIdAndMessageType(conversationId, messageType)
            .sortedByDescending { it.createdAt }
            .map { it.toMessageResponse() }
    }

    fun getMediaMessages(conversationId: String): List<MessageResponse> {
        return messageRepository.findByConversationIdAndMediaUrlIsNotNull(conversationId)
            .sortedByDescending { it.createdAt }
            .map { it.toMessageResponse() }
    }
}

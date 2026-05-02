package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Message
import com.example.duralap.database.model.MessageType
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.events.MessageCreatedEvent
import com.example.duralap.service.cache.ConversationValidationCache
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class MessageService(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val conversationValidator: ConversationValidationCache,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val conversationRequestService: ConversationRequestService,
    private val rateLimitingService: RateLimitingService,
    private val userCache: com.example.duralap.service.cache.UserCache
) {

    private val MESSAGE_EVENTS_TOPIC = "chat.events.message.created"

    /**
     * CQRS Command Side: Persists to MongoDB immediately and then emits an event to Kafka.
     * This ensures the message is saved for "later view" and available for real-time delivery.
     * Also updates the parent Conversation metadata for optimized listing.
     * 
     * VALIDATION: Only allows sending messages if conversation is ACCEPTED
     * RATE LIMITING: Prevents message spam
     */
    fun sendMessage(request: MessageCreateRequest): MessageResponse {
        // 0. Rate limiting check
        if (!rateLimitingService.canSendMessage(request.senderId)) {
            throw IllegalArgumentException("Rate limit exceeded. Please wait before sending more messages.")
        }
        
        val transactionId = UUID.randomUUID().toString()
        val timestamp = Instant.now()
        
        // 0. Verify conversation exists and is ACCEPTED
        val conversation = conversationRepository.findByIdOrNull(request.conversationId)
            ?: throw IllegalArgumentException("Conversation not found")
        
        if (conversation.status != com.example.duralap.database.model.ConversationStatus.ACCEPTED) {
            throw IllegalArgumentException("Cannot send message. Conversation request is still pending or was rejected.")
        }
        
        // 1. Fast, highly available caching layer lookup (Redis)
        conversationValidator.verifyUserParticipantInConversation(
            userId = request.senderId, 
            conversationId = request.conversationId
        )

        // 2. Persist to MongoDB immediately (Guarantee storage)
        val message = Message(
            id = transactionId,
            conversationId = request.conversationId,
            senderId = request.senderId,
            content = request.content,
            messageType = request.messageType,
            mediaUrl = request.mediaUrl,
            mediaType = request.mediaType,
            fileName = request.fileName,
            fileSize = request.fileSize,
            isRead = false,
            createdAt = timestamp,
            updatedAt = timestamp
        )
        val savedMessage = messageRepository.save(message)

        // 3. Update Conversation Metadata for "Later View" optimization
        conversationRepository.findByIdOrNull(request.conversationId)?.let { conversation ->
            conversation.lastMessageId = savedMessage.id
            conversation.lastMessageSenderId = savedMessage.senderId
            conversation.lastMessageContent = savedMessage.content
            conversation.lastMessageType = savedMessage.messageType
            conversation.lastMessageAt = timestamp
            conversationRepository.save(conversation)
        }

        // 4. Fire to Kafka for real-time delivery
        val event = MessageCreatedEvent(
            id = transactionId,
            conversationId = request.conversationId,
            senderId = request.senderId,
            content = request.content,
            messageType = request.messageType,
            mediaUrl = request.mediaUrl,
            mediaType = request.mediaType,
            fileName = request.fileName,
            fileSize = request.fileSize,
            timestamp = timestamp
        )
        kafkaTemplate.send(MESSAGE_EVENTS_TOPIC, request.conversationId, event)

        // 5. Return the saved message response
        return savedMessage.toMessageResponse()
    }

    /**
     * CQRS Query Side: Reads exclusively from secondary nodes/replicas.
     * Heavily indexed on { conversationId: 1, createdAt: -1 }
     */
    fun getMessages(conversationId: String, page: Int = 0, size: Int = 20): List<MessageResponse> {
        val pageable: Pageable = PageRequest.of(page, size)
        val messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable).content
        return messages.reversed().map { it.toMessageResponse() }
    }

    /**
     * Get all messages for a conversation (without pagination)
     */
    fun getAllMessages(conversationId: String): List<MessageResponse> {
        val messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId)
        return messages.reversed().map { it.toMessageResponse() }
    }

    /**
     * Get message by ID
     */
    fun getMessageById(id: String): MessageResponse? {
        return messageRepository.findByIdOrNull(id)?.toMessageResponse()
    }

    /**
     * Mark message as read
     */
    fun markMessageAsRead(messageId: String, userId: String): MessageResponse {
        val message = messageRepository.findByIdOrNull(messageId)
            ?: throw IllegalArgumentException("Message not found")

        conversationValidator.verifyUserParticipantInConversation(userId, message.conversationId)

        if (message.senderId != userId) {
            val updatedMessage = message.copy(
                isRead = true,
                readAt = Instant.now(),
                updatedAt = Instant.now()
            )

            val savedMessage = messageRepository.save(updatedMessage)
            return savedMessage.toMessageResponse()
        }

        return message.toMessageResponse()
    }

    /**
     * Mark all messages in conversation as read
     */
    fun markAllMessagesAsRead(conversationId: String, userId: String) {
        val messages = messageRepository.markMessagesAsRead(conversationId, userId)
        messages.forEach { message ->
            val updatedMessage = message.copy(
                isRead = true,
                readAt = Instant.now(),
                updatedAt = Instant.now()
            )
            messageRepository.save(updatedMessage)
        }
    }

    /**
     * Get unread messages count for a user in a conversation
     */
    fun getUnreadMessagesCount(conversationId: String, userId: String): Long {
        return messageRepository.countUnreadMessages(conversationId, userId)
    }

    /**
     * Get unread messages for a user in a conversation
     */
    fun getUnreadMessages(conversationId: String, userId: String): List<MessageResponse> {
        val messages = messageRepository.findUnreadMessages(conversationId, userId)
        return messages.map { it.toMessageResponse() }
    }

    /**
     * Get last message in a conversation
     */
    fun getLastMessage(conversationId: String): MessageResponse? {
        val message = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
        return message?.toMessageResponse()
    }

    /**
     * Delete message
     */
    fun deleteMessage(id: String, userId: String): Boolean {
        val message = messageRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Message not found")

        if (message.senderId != userId) {
            throw IllegalArgumentException("Only sender can delete the message")
        }

        messageRepository.deleteById(id)
        return true
    }

    /**
     * Get messages by type
     */
    fun getMessagesByType(conversationId: String, messageType: MessageType): List<MessageResponse> {
        return messageRepository.findByConversationIdAndMessageType(conversationId, messageType)
            .sortedByDescending { it.createdAt }
            .map { it.toMessageResponse() }
    }

    /**
     * Get messages with media in a conversation
     */
    fun getMediaMessages(conversationId: String): List<MessageResponse> {
        return messageRepository.findByConversationIdAndMediaUrlIsNotNull(conversationId)
            .sortedByDescending { it.createdAt }
            .map { it.toMessageResponse() }
    }
}

/**
 * Extension function to convert Message to MessageResponse
 */
fun Message.toMessageResponse(): MessageResponse {
    return MessageResponse(
        id = this.id ?: throw IllegalStateException("Message ID cannot be null"),
        conversationId = this.conversationId,
        senderId = this.senderId,
        content = this.content,
        messageType = this.messageType,
        mediaUrl = this.mediaUrl,
        mediaType = this.mediaType,
        fileName = this.fileName,
        fileSize = this.fileSize,
        isRead = this.isRead,
        readAt = this.readAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        senderInfo = null // Will be populated by service when needed
    )
}

/**
 * Extension function to convert User to UserInfo
 */
fun com.example.duralap.database.model.User.toUserInfo(): UserInfo {
    return UserInfo(
        id = this.id ?: throw IllegalStateException("User ID cannot be null"),
        username = this.username,
        fullName = this.fullName,
        profileImageUrl = this.profileImageUrl
    )
}
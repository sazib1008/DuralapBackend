package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Conversation
import com.example.duralap.database.model.ConversationRequest
import com.example.duralap.database.model.ConversationStatus
import com.example.duralap.database.model.MessageType
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.ConversationRequestRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.events.ConversationCreatedEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ConversationRequestService(
    private val conversationRequestRepository: ConversationRequestRepository,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val rateLimitingService: com.example.duralap.service.RateLimitingService,
    private val userCache: com.example.duralap.service.cache.UserCache
) {

    private val CONVERSATION_EVENTS_TOPIC = "chat.events.conversation.created"

    /**
     * Create or get a conversation request when User A wants to message User B for the first time.
     * If a conversation already exists and is accepted, return it directly.
     * If a pending request exists, return it.
     * Otherwise, create a new pending conversation request.
     * 
     * RATE LIMITING: Prevents spam conversation requests
     */
    fun createConversationRequest(
        senderId: String,
        recipientId: String,
        initialMessage: String? = null
    ): ConversationRequestResponse {
        // Rate limiting check
        if (!rateLimitingService.canCreateConversationRequest(senderId)) {
            throw IllegalArgumentException("Rate limit exceeded. Too many conversation requests. Please try later.")
        }
        
        // Validate users exist
        if (!userRepository.existsById(senderId)) {
            throw IllegalArgumentException("Sender does not exist")
        }
        if (!userRepository.existsById(recipientId)) {
            throw IllegalArgumentException("Recipient does not exist")
        }

        if (senderId == recipientId) {
            throw IllegalArgumentException("Cannot create conversation with yourself")
        }

        // Check if an accepted conversation already exists
        val existingConversations = conversationRepository
            .findByParticipantIdsContainingAndParticipantIdsContaining(senderId, recipientId)
        
        val acceptedConversation = existingConversations.find { it.status == ConversationStatus.ACCEPTED }
        if (acceptedConversation != null) {
            // Return existing accepted conversation
            return ConversationRequestResponse(
                id = "existing",
                senderId = senderId,
                recipientId = recipientId,
                conversationId = acceptedConversation.id!!,
                status = ConversationStatus.ACCEPTED,
                requestedAt = acceptedConversation.createdAt
            )
        }

        // Check if there's already a pending request
        val existingPendingRequest = conversationRequestRepository
            .findPendingRequest(senderId, recipientId)
            .orElseGet {
                conversationRequestRepository.findPendingRequest(recipientId, senderId).orElse(null)
            }

        if (existingPendingRequest != null) {
            return existingPendingRequest.toConversationRequestResponse(userRepository)
        }

        // Create new conversation in PENDING status
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = setOf(senderId, recipientId),
            status = ConversationStatus.PENDING,
            createdAt = Instant.now()
        )
        val savedConversation = conversationRepository.save(conversation)

        // Create conversation request
        val request = ConversationRequest(
            id = UUID.randomUUID().toString(),
            senderId = senderId,
            recipientId = recipientId,
            conversationId = savedConversation.id!!,
            status = ConversationStatus.PENDING,
            initialMessage = initialMessage,
            requestedAt = Instant.now()
        )
        val savedRequest = conversationRequestRepository.save(request)

        // Send initial message if provided (will only be visible after acceptance)
        if (!initialMessage.isNullOrBlank()) {
            val message = com.example.duralap.database.model.Message(
                id = UUID.randomUUID().toString(),
                conversationId = savedConversation.id!!,
                senderId = senderId,
                content = initialMessage,
                messageType = MessageType.TEXT,
                isRead = false,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            messageRepository.save(message)
        }

        // Publish event to notify recipient
        val event = ConversationCreatedEvent(
            id = savedConversation.id!!,
            participantIds = savedConversation.participantIds,
            createdAt = savedConversation.createdAt
        )
        kafkaTemplate.send(CONVERSATION_EVENTS_TOPIC, savedConversation.id, event)

        return savedRequest.toConversationRequestResponse(userRepository)
    }

    /**
     * Accept a conversation request
     */
    fun acceptConversationRequest(requestId: String, userId: String): ConversationRequestResponse {
        val request = conversationRequestRepository.findByIdOrNull(requestId)
            ?: throw IllegalArgumentException("Conversation request not found")

        if (request.status != ConversationStatus.PENDING) {
            throw IllegalArgumentException("Request is not pending")
        }

        if (request.recipientId != userId) {
            throw IllegalArgumentException("Only the recipient can accept the request")
        }

        // Update request status
        val updatedRequest = request.copy(
            status = ConversationStatus.ACCEPTED,
            respondedAt = Instant.now(),
            respondedBy = userId
        )
        conversationRequestRepository.save(updatedRequest)

        // Update conversation status
        val conversation = conversationRepository.findByIdOrNull(request.conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(status = ConversationStatus.ACCEPTED)
            conversationRepository.save(updatedConversation)
        }

        // Mark initial message as delivered (if exists)
        if (!request.initialMessage.isNullOrBlank()) {
            messageRepository.findByConversationIdOrderByCreatedAtDesc(request.conversationId)
                .firstOrNull()?.let { message ->
                    val updatedMessage = message.copy(isRead = false)
                    messageRepository.save(updatedMessage)
                }
        }

        return updatedRequest.toConversationRequestResponse(userRepository)
    }

    /**
     * Reject a conversation request
     */
    fun rejectConversationRequest(requestId: String, userId: String): ConversationRequestResponse {
        val request = conversationRequestRepository.findByIdOrNull(requestId)
            ?: throw IllegalArgumentException("Conversation request not found")

        if (request.status != ConversationStatus.PENDING) {
            throw IllegalArgumentException("Request is not pending")
        }

        if (request.recipientId != userId) {
            throw IllegalArgumentException("Only the recipient can reject the request")
        }

        // Update request status
        val updatedRequest = request.copy(
            status = ConversationStatus.REJECTED,
            respondedAt = Instant.now(),
            respondedBy = userId
        )
        conversationRequestRepository.save(updatedRequest)

        // Update conversation status
        val conversation = conversationRepository.findByIdOrNull(request.conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(status = ConversationStatus.REJECTED)
            conversationRepository.save(updatedConversation)
        }

        // Delete the conversation and messages (clean up rejected requests)
        conversationRepository.deleteById(request.conversationId)
        messageRepository.deleteByConversationId(request.conversationId)

        return updatedRequest.toConversationRequestResponse(userRepository)
    }

    /**
     * Get all pending requests for a user (received)
     */
    fun getPendingRequestsForUser(userId: String): List<ConversationRequestResponse> {
        return conversationRequestRepository
            .findPendingRequestsForRecipient(userId)
            .map { it.toConversationRequestResponse(userRepository) }
    }

    /**
     * Get count of pending requests for a user
     */
    fun getPendingRequestCount(userId: String): Long {
        return conversationRequestRepository.countPendingRequestsForRecipient(userId)
    }

    /**
     * Check if user can message another user (conversation must be accepted)
     */
    fun canUserMessage(userId: String, targetUserId: String): Boolean {
        val conversations = conversationRepository
            .findByParticipantIdsContainingAndParticipantIdsContaining(userId, targetUserId)
        
        return conversations.any { it.status == ConversationStatus.ACCEPTED }
    }

    /**
     * Cancel a sent conversation request (before recipient responds)
     */
    fun cancelConversationRequest(requestId: String, userId: String): Boolean {
        val request = conversationRequestRepository.findByIdOrNull(requestId)
            ?: throw IllegalArgumentException("Conversation request not found")

        if (request.status != ConversationStatus.PENDING) {
            throw IllegalArgumentException("Request is not pending")
        }

        if (request.senderId != userId) {
            throw IllegalArgumentException("Only the sender can cancel the request")
        }

        // Delete request and conversation
        conversationRequestRepository.deleteById(requestId)
        conversationRepository.deleteById(request.conversationId)
        messageRepository.deleteByConversationId(request.conversationId)

        return true
    }
}

/**
 * Extension function to convert ConversationRequest to ConversationRequestResponse
 */
fun ConversationRequest.toConversationRequestResponse(userRepository: UserRepository): ConversationRequestResponse {
    val sender = userRepository.findByIdOrNull(this.senderId)
    
    return ConversationRequestResponse(
        id = this.id ?: throw IllegalStateException("Request ID cannot be null"),
        senderId = this.senderId,
        senderUsername = sender?.username,
        senderFullName = sender?.fullName,
        senderProfileImageUrl = sender?.profileImageUrl,
        recipientId = this.recipientId,
        conversationId = this.conversationId,
        status = this.status,
        initialMessage = this.initialMessage,
        requestedAt = this.requestedAt,
        respondedAt = this.respondedAt
    )
}

package com.example.duralap.chat.application.service

import com.example.duralap.chat.domain.model.*
import com.example.duralap.chat.domain.repository.ConversationRepository
import com.example.duralap.chat.domain.repository.ConversationRequestRepository
import com.example.duralap.config.RateLimitingService
import com.example.duralap.database.dto.*
import com.example.duralap.database.model.*
import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ConversationRequestService(
    private val conversationRequestRepository: ConversationRequestRepository,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val rateLimitingService: RateLimitingService,
    private val eventPublisher: ApplicationEventPublisher,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    fun createConversationRequest(
        senderId: String,
        recipientId: String,
        initialMessage: String? = null
    ): ConversationRequestResponse {
        if (!rateLimitingService.canCreateConversationRequest(senderId)) {
            throw IllegalArgumentException("Rate limit exceeded. Too many conversation requests. Please try later.")
        }
        
        if (!userRepository.existsById(senderId)) {
            throw IllegalArgumentException("Sender does not exist")
        }
        if (!userRepository.existsById(recipientId)) {
            throw IllegalArgumentException("Recipient does not exist")
        }

        if (senderId == recipientId) {
            throw IllegalArgumentException("Cannot create conversation with yourself")
        }

        val existingConversations = conversationRepository
            .findByParticipantIdsContainingAndParticipantIdsContaining(senderId, recipientId)
        
        val acceptedConversation = existingConversations.find { it.status == ConversationStatus.ACCEPTED }
        if (acceptedConversation != null) {
            return ConversationRequestResponse(
                id = "existing",
                senderId = senderId,
                recipientId = recipientId,
                conversationId = acceptedConversation.id!!,
                status = ConversationStatus.ACCEPTED,
                requestedAt = acceptedConversation.createdAt
            )
        }

        val existingPendingRequest = conversationRequestRepository
            .findPendingRequest(senderId, recipientId)
            .orElseGet {
                conversationRequestRepository.findPendingRequest(recipientId, senderId).orElse(null)
            }

        if (existingPendingRequest != null) {
            val sender = userRepository.findByIdOrNull(existingPendingRequest.senderId)
            return existingPendingRequest.toConversationRequestResponse(sender)
        }

        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = setOf(senderId, recipientId),
            status = ConversationStatus.PENDING,
            createdAt = Instant.now()
        )
        val savedConversation = conversationRepository.save(conversation)

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

        val event = ConversationCreatedEvent(
            id = savedConversation.id!!,
            participantIds = savedConversation.participantIds,
            createdAt = savedConversation.createdAt
        )
        eventPublisher.publishEvent(event)

        val sender = userRepository.findByIdOrNull(senderId)
        return savedRequest.toConversationRequestResponse(sender)
    }

    fun acceptConversationRequest(requestId: String, userId: String): ConversationRequestResponse {
        val request = conversationRequestRepository.findByIdOrNull(requestId)
            ?: throw IllegalArgumentException("Conversation request not found")

        if (request.status != ConversationStatus.PENDING) {
            throw IllegalArgumentException("Request is not pending")
        }

        if (request.recipientId != userId) {
            throw IllegalArgumentException("Only the recipient can accept the request")
        }

        val updatedRequest = request.copy(
            status = ConversationStatus.ACCEPTED,
            respondedAt = Instant.now(),
            respondedBy = userId
        )
        conversationRequestRepository.save(updatedRequest)

        val conversation = conversationRepository.findByIdOrNull(request.conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(status = ConversationStatus.ACCEPTED)
            conversationRepository.save(updatedConversation)
        }

        val sender = userRepository.findByIdOrNull(request.senderId)
        return updatedRequest.toConversationRequestResponse(sender)
    }

    fun rejectConversationRequest(requestId: String, userId: String): ConversationRequestResponse {
        val request = conversationRequestRepository.findByIdOrNull(requestId)
            ?: throw IllegalArgumentException("Conversation request not found")

        if (request.status != ConversationStatus.PENDING) {
            throw IllegalArgumentException("Request is not pending")
        }

        if (request.recipientId != userId) {
            throw IllegalArgumentException("Only the recipient can reject the request")
        }

        val updatedRequest = request.copy(
            status = ConversationStatus.REJECTED,
            respondedAt = Instant.now(),
            respondedBy = userId
        )
        conversationRequestRepository.save(updatedRequest)

        val conversation = conversationRepository.findByIdOrNull(request.conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(status = ConversationStatus.REJECTED)
            conversationRepository.save(updatedConversation)
        }

        conversationRepository.deleteById(request.conversationId)

        val sender = userRepository.findByIdOrNull(request.senderId)
        return updatedRequest.toConversationRequestResponse(sender)
    }

    fun getPendingRequestsForUser(userId: String): List<ConversationRequestResponse> {
        return conversationRequestRepository
            .findPendingRequestsForRecipient(userId)
            .map {
                val sender = userRepository.findByIdOrNull(it.senderId)
                it.toConversationRequestResponse(sender)
            }
    }

    fun getPendingRequestCount(userId: String): Long {
        return conversationRequestRepository.countPendingRequestsForRecipient(userId)
    }

    fun canUserMessage(userId: String, targetUserId: String): Boolean {
        val conversations = conversationRepository
            .findByParticipantIdsContainingAndParticipantIdsContaining(userId, targetUserId)
        
        return conversations.any { it.status == ConversationStatus.ACCEPTED }
    }

    fun cancelConversationRequest(requestId: String, userId: String): Boolean {
        val request = conversationRequestRepository.findByIdOrNull(requestId)
            ?: throw IllegalArgumentException("Conversation request not found")

        if (request.status != ConversationStatus.PENDING) {
            throw IllegalArgumentException("Request is not pending")
        }

        if (request.senderId != userId) {
            throw IllegalArgumentException("Only the sender can cancel the request")
        }

        conversationRequestRepository.deleteById(requestId)
        conversationRepository.deleteById(request.conversationId)

        return true
    }
}

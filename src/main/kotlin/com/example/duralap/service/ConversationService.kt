package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Conversation
import com.example.duralap.database.model.UserConversations
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.database.repository.UserConversationsRepository
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.events.ConversationCreatedEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val userConversationsRepository: UserConversationsRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun updateUserConversations(userId: String, conversationId: String, add: Boolean = true) {
        val userConversations = userConversationsRepository.findById(userId).orElseGet {
            // Check if conversation history is already there but mapping missing
            val existingConvs = conversationRepository.findByParticipantIdsContaining(userId)
            val ids = existingConvs.mapNotNull { it.id }.toSet()
            UserConversations(userId, ids)
        }
        
        val updatedIds = if (add) {
            userConversations.conversationIds + conversationId
        } else {
            userConversations.conversationIds - conversationId
        }
        
        userConversationsRepository.save(userConversations.copy(conversationIds = updatedIds))
    }

    private val CONVERSATION_EVENTS_TOPIC = "chat.events.conversation.created"

    /**
     * Create a new conversation
     */
    fun createConversation(request: ConversationCreateRequest): ConversationResponse {
        // Validate that all participants exist
        request.participantIds.forEach { userId ->
            if (!userRepository.existsById(userId)) {
                throw IllegalArgumentException("User with ID $userId does not exist")
            }
        }

        // Check if conversation already exists
        val existingConversations = conversationRepository.findByParticipantIds(request.participantIds, request.participantIds.size)
        if (existingConversations.isNotEmpty()) {
            return existingConversations.first().toConversationResponse()
        }

        // Create new conversation
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = request.participantIds,
            createdAt = Instant.now()
        )

        val savedConversation = conversationRepository.save(conversation)
        
        // Publish event to notify participants
        val event = ConversationCreatedEvent(
            id = savedConversation.id!!,
            participantIds = savedConversation.participantIds,
            createdAt = savedConversation.createdAt
        )
        kafkaTemplate.send(CONVERSATION_EVENTS_TOPIC, savedConversation.id, event)
        
        return savedConversation.toConversationResponse()
    }


    /**
     * Get or create conversation between two users (WhatsApp-like behavior)
     */
    fun getOrCreateConversation(user1Id: String, user2Id: String): ConversationResponse {
        // Validate users exist
        if (!userRepository.existsById(user1Id)) {
            throw IllegalArgumentException("User with ID $user1Id does not exist")
        }
        if (!userRepository.existsById(user2Id)) {
            throw IllegalArgumentException("User with ID $user2Id does not exist")
        }

        // Find existing conversation
        val existing = conversationRepository
            .findByParticipantIdsContainingAndParticipantIdsContaining(user1Id, user2Id)

        if (existing.isNotEmpty()) {
            return existing.first().toConversationResponse()
        }

        // Create new conversation
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = setOf(user1Id, user2Id),
            createdAt = Instant.now()
        )

        val savedConversation = conversationRepository.save(conversation)
        
        // Publish event to notify participants
        val event = ConversationCreatedEvent(
            id = savedConversation.id!!,
            participantIds = savedConversation.participantIds,
            createdAt = savedConversation.createdAt
        )
        kafkaTemplate.send(CONVERSATION_EVENTS_TOPIC, savedConversation.id, event)
        
        return savedConversation.toConversationResponse()
    }


    /**
     * Get or create conversation by target username (for the authenticated user)
     */
    fun getOrCreateByUsername(currentUserId: String, targetUsername: String): ConversationResponse {
        val targetUser = userRepository.findByUsername(targetUsername.lowercase())
            .orElseThrow { IllegalArgumentException("User with username $targetUsername does not exist") }
        
        return getOrCreateConversation(currentUserId, targetUser.id!!)
    }


    /**
     * Get conversation by ID
     */
    fun getConversationById(id: String): ConversationResponse? {
        return conversationRepository.findByIdOrNull(id)?.toConversationResponse()
    }

    fun getUserConversationIds(userId: String): Set<String> {
        val userConversations = userConversationsRepository.findById(userId).orElseGet {
            // Backfill if not exists
            val existingConvs = conversationRepository.findByParticipantIdsContaining(userId)
            val ids = existingConvs.mapNotNull { it.id }.toSet()
            userConversationsRepository.save(UserConversations(userId, ids))
        }
        return userConversations.conversationIds
    }

    /**
     * Get all conversations for a user (only ACCEPTED conversations)
     */
    fun getConversationsForUser(userId: String): List<ConversationResponse> {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User with ID $userId does not exist")
        }

        val conversationIds = getUserConversationIds(userId)
        val conversations = conversationRepository.findAllById(conversationIds)
        
        // Filter to only show ACCEPTED conversations
        val acceptedConversations = conversations.filter { it.status == com.example.duralap.database.model.ConversationStatus.ACCEPTED }
        val sortedConversations = acceptedConversations.sortedByDescending { it.lastMessageAt ?: it.createdAt }

        return sortedConversations.map { conversation ->
            val unreadCount = messageRepository.countUnreadMessages(conversation.id!!, userId).toInt()
            
            // Build the last message response from cached metadata if available
            val lastMessage = if (conversation.lastMessageId != null) {
                MessageResponse(
                    id = conversation.lastMessageId!!,
                    conversationId = conversation.id,
                    senderId = conversation.lastMessageSenderId!!,
                    content = conversation.lastMessageContent!!,
                    messageType = conversation.lastMessageType!!,
                    mediaUrl = null, // Cached metadata is minimal
                    mediaType = null,
                    fileName = null,
                    fileSize = null,
                    isRead = true, // We don't track per-user read status in cache yet
                    readAt = null,
                    createdAt = conversation.lastMessageAt!!,
                    updatedAt = conversation.lastMessageAt!!
                )
            } else null
            
            conversation.toConversationResponse(lastMessage, unreadCount)
        }
    }

    /**
     * Delete conversation
     */
    fun deleteConversation(id: String) {
        val conversation = conversationRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Conversation not found")
            
        conversationRepository.deleteById(id)
        
        // Remove mapping from all participants
        conversation.participantIds.forEach { userId ->
            updateUserConversations(userId, id, add = false)
        }
    }

    /**
     * Add participant to conversation
     */
    fun addParticipant(conversationId: String, userId: String): ConversationResponse {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: throw IllegalArgumentException("Conversation not found")

        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User with ID $userId does not exist")
        }

        if (conversation.participantIds.contains(userId)) {
            throw IllegalArgumentException("User is already a participant")
        }

        val updatedConversation = conversation.copy(
            participantIds = conversation.participantIds + userId
        )

        val savedConversation = conversationRepository.save(updatedConversation)
        
        // Update user conversations mapping for newly added participant
        updateUserConversations(userId, conversationId)

        return savedConversation.toConversationResponse()
    }

    /**
     * Remove participant from conversation
     */
    fun removeParticipant(conversationId: String, userId: String): ConversationResponse {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: throw IllegalArgumentException("Conversation not found")

        if (!conversation.participantIds.contains(userId)) {
            throw IllegalArgumentException("User is not a participant")
        }

        if (conversation.participantIds.size <= 2) {
            throw IllegalArgumentException("Cannot remove participant from a two-person conversation")
        }

        val updatedConversation = conversation.copy(
            participantIds = conversation.participantIds - userId
        )

        val savedConversation = conversationRepository.save(updatedConversation)
        
        // Remove user configurations mapping for the removed participant
        updateUserConversations(userId, conversationId, add = false)

        return savedConversation.toConversationResponse()
    }

    /**
     * Check if user is participant in conversation
     */
    fun isUserParticipant(conversationId: String, userId: String): Boolean {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: return false
        return conversation.participantIds.contains(userId)
    }

    /**
     * Get conversation participants
     */
    fun getConversationParticipants(conversationId: String): List<UserInfo> {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: throw IllegalArgumentException("Conversation not found")

        return conversation.participantIds.mapNotNull { userId ->
            userRepository.findByIdOrNull(userId)?.toUserInfo()
        }
    }
}

/**
 * Extension function to convert Conversation to ConversationResponse
 */
fun Conversation.toConversationResponse(
    lastMessage: MessageResponse? = null,
    unreadCount: Int = 0
): ConversationResponse {
    return ConversationResponse(
        id = this.id ?: throw IllegalStateException("Conversation ID cannot be null"),
        participantIds = this.participantIds,
        status = this.status,
        createdAt = this.createdAt,
        lastMessage = lastMessage,
        unreadCount = unreadCount
    )
}
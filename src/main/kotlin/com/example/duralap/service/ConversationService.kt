package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Conversation
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.database.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) {

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
        val existingConversation = conversationRepository.findByParticipantIds(request.participantIds)
        if (existingConversation.isPresent) {
            return existingConversation.get().toConversationResponse()
        }

        // Create new conversation
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = request.participantIds,
            createdAt = Instant.now()
        )

        val savedConversation = conversationRepository.save(conversation)
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

        if (existing.isPresent) {
            return existing.get().toConversationResponse()
        }

        // Create new conversation
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = setOf(user1Id, user2Id),
            createdAt = Instant.now()
        )

        val savedConversation = conversationRepository.save(conversation)
        return savedConversation.toConversationResponse()
    }

    /**
     * Get conversation by ID
     */
    fun getConversationById(id: String): ConversationResponse? {
        return conversationRepository.findByIdOrNull(id)?.toConversationResponse()
    }

    /**
     * Get all conversations for a user
     */
    fun getConversationsForUser(userId: String): List<ConversationResponse> {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User with ID $userId does not exist")
        }

        val conversations = conversationRepository.findByParticipantIdsContaining(userId)
        return conversations.map { conversation ->
            val lastMessage = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.id!!)
            val unreadCount = messageRepository.countUnreadMessages(conversation.id, userId).toInt()
            
            conversation.toConversationResponse(lastMessage?.toMessageResponse(), unreadCount)
        }
    }

    /**
     * Delete conversation
     */
    fun deleteConversation(id: String) {
        if (!conversationRepository.existsById(id)) {
            throw IllegalArgumentException("Conversation not found")
        }
        conversationRepository.deleteById(id)
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
        createdAt = this.createdAt,
        lastMessage = lastMessage,
        unreadCount = unreadCount
    )
}
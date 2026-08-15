package com.example.duralap.chat.application.service

import com.example.duralap.chat.domain.model.*
import com.example.duralap.chat.domain.repository.ConversationRepository
import com.example.duralap.chat.domain.repository.UserConversationsRepository
import com.example.duralap.database.dto.*
import com.example.duralap.database.model.*
import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.user.api.UserModuleApi
import com.example.duralap.user.domain.model.User
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository,
    private val userConversationsRepository: UserConversationsRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val userModuleApi: UserModuleApi
) {

    fun updateUserConversations(userId: String, conversationId: String, add: Boolean = true) {
        val userConversations = userConversationsRepository.findById(userId).orElseGet {
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

    fun createConversation(request: ConversationCreateRequest): ConversationResponse {
        request.participantIds.forEach { userId ->
            if (!userModuleApi.existsById(userId)) {
                throw IllegalArgumentException("User with ID $userId does not exist")
            }
        }

        val existingConversations = conversationRepository.findByParticipantIds(request.participantIds, request.participantIds.size)
        if (existingConversations.isNotEmpty()) {
            return existingConversations.first().toConversationResponse()
        }

        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = request.participantIds,
            createdAt = Instant.now()
        )

        val savedConversation = conversationRepository.save(conversation)
        
        val event = ConversationCreatedEvent(
            id = savedConversation.id!!,
            participantIds = savedConversation.participantIds,
            createdAt = savedConversation.createdAt
        )
        eventPublisher.publishEvent(event)

        savedConversation.participantIds.forEach { userId ->
            updateUserConversations(userId, savedConversation.id!!)
            val destination = "/user/$userId/queue/conversations"
            simpMessagingTemplate.convertAndSend(destination, event)
        }
        
        return savedConversation.toConversationResponse()
    }

    fun getOrCreateConversation(user1Id: String, user2Id: String): ConversationResponse {
        if (!userModuleApi.existsById(user1Id)) {
            throw IllegalArgumentException("User with ID $user1Id does not exist")
        }
        if (!userModuleApi.existsById(user2Id)) {
            throw IllegalArgumentException("User with ID $user2Id does not exist")
        }

        val existing = conversationRepository
            .findByParticipantIdsContainingAndParticipantIdsContaining(user1Id, user2Id)

        if (existing.isNotEmpty()) {
            return existing.first().toConversationResponse()
        }

        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            participantIds = setOf(user1Id, user2Id),
            createdAt = Instant.now()
        )

        val savedConversation = conversationRepository.save(conversation)
        
        val event = ConversationCreatedEvent(
            id = savedConversation.id!!,
            participantIds = savedConversation.participantIds,
            createdAt = savedConversation.createdAt
        )
        eventPublisher.publishEvent(event)

        savedConversation.participantIds.forEach { userId ->
            updateUserConversations(userId, savedConversation.id!!)
            val destination = "/user/$userId/queue/conversations"
            simpMessagingTemplate.convertAndSend(destination, event)
        }
        
        return savedConversation.toConversationResponse()
    }

    fun getOrCreateByUsername(currentUserId: String, targetUsername: String): ConversationResponse {
        val targetUser = userModuleApi.findUserByUsername(targetUsername.lowercase())
            ?: throw IllegalArgumentException("User with username $targetUsername does not exist")
        
        return getOrCreateConversation(currentUserId, targetUser.id)
    }

    fun getConversationById(id: String): ConversationResponse? {
        return conversationRepository.findByIdOrNull(id)?.toConversationResponse()
    }

    fun getUserConversationIds(userId: String): Set<String> {
        val userConversations = userConversationsRepository.findById(userId).orElseGet {
            val existingConvs = conversationRepository.findByParticipantIdsContaining(userId)
            val ids = existingConvs.mapNotNull { it.id }.toSet()
            userConversationsRepository.save(UserConversations(userId, ids))
        }
        return userConversations.conversationIds
    }

    fun getConversationsForUser(userId: String): List<ConversationResponse> {
        if (!userModuleApi.existsById(userId)) {
            throw IllegalArgumentException("User with ID $userId does not exist")
        }

        val mappedIds = getUserConversationIds(userId)
        val directConvs = conversationRepository.findByParticipantIdsContaining(userId)
        var allConvs = (conversationRepository.findAllById(mappedIds) + directConvs).distinctBy { it.id }

        if (allConvs.isEmpty()) {
            allConvs = seedSampleConversationsForUser(userId)
        }
        
        val acceptedConversations = allConvs.filter { 
            it.status == ConversationStatus.ACCEPTED 
        }
        val sortedConversations = acceptedConversations.sortedByDescending { it.lastMessageAt ?: it.createdAt }

        return sortedConversations.map { conversation ->
            val lastMessage = if (conversation.lastMessageId != null) {
                val senderUser = userModuleApi.findUserById(conversation.lastMessageSenderId!!)
                MessageResponse(
                    id = conversation.lastMessageId!!,
                    conversationId = conversation.id!!,
                    senderId = conversation.lastMessageSenderId!!,
                    content = conversation.lastMessageContent!!,
                    messageType = conversation.lastMessageType!!,
                    mediaUrl = null,
                    mediaType = null,
                    fileName = null,
                    fileSize = null,
                    isRead = true,
                    readAt = null,
                    createdAt = conversation.lastMessageAt!!,
                    updatedAt = conversation.lastMessageAt!!,
                    senderInfo = senderUser?.toUserInfo(),
                    clientMsgId = null,
                    status = MessageStatus.SENT
                )
            } else null
            
            val participants = userModuleApi.findUserInfosByIds(conversation.participantIds)
            
            conversation.toConversationResponse(lastMessage, 0, participants)
        }
    }

    private fun seedSampleConversationsForUser(userId: String): List<Conversation> {
        val sampleContacts = listOf(
            Triple("elena_gilbert", "Elena Gilbert", "The final designs are ready for review!"),
            Triple("design_team", "Design Team", "Marcus: Check out the new mobile components."),
            Triple("alex_rivera", "Alex Rivera", "See you at the meeting tomorrow!")
        )

        val createdConversations = mutableListOf<Conversation>()

        for ((username, fullName, sampleMsg) in sampleContacts) {
            val demoUser = userRepository.findByUsername(username).orElseGet {
                userRepository.save(
                    User(
                        id = UUID.randomUUID().toString(),
                        username = username,
                        email = "$username@duralap.com",
                        fullName = fullName,
                        status = UserStatus.ONLINE
                    )
                )
            }

            val convId = UUID.randomUUID().toString()
            val now = Instant.now().minusSeconds((createdConversations.size + 1) * 3600L)
            val msgId = UUID.randomUUID().toString()

            val conversation = Conversation(
                id = convId,
                participantIds = setOf(userId, demoUser.id!!),
                status = ConversationStatus.ACCEPTED,
                createdAt = now,
                lastMessageId = msgId,
                lastMessageSenderId = demoUser.id!!,
                lastMessageContent = sampleMsg,
                lastMessageType = MessageType.TEXT,
                lastMessageAt = now
            )
            val savedConv = conversationRepository.save(conversation)
            updateUserConversations(userId, convId)
            updateUserConversations(demoUser.id!!, convId)

            createdConversations.add(savedConv)
        }

        return createdConversations
    }

    fun deleteConversation(id: String) {
        val conversation = conversationRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Conversation not found")
            
        conversationRepository.deleteById(id)
        
        conversation.participantIds.forEach { userId ->
            updateUserConversations(userId, id, add = false)
        }
    }

    fun addParticipant(conversationId: String, userId: String): ConversationResponse {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: throw IllegalArgumentException("Conversation not found")

        if (!userModuleApi.existsById(userId)) {
            throw IllegalArgumentException("User with ID $userId does not exist")
        }

        if (conversation.participantIds.contains(userId)) {
            throw IllegalArgumentException("User is already a participant")
        }

        val updatedConversation = conversation.copy(
            participantIds = conversation.participantIds + userId
        )

        val savedConversation = conversationRepository.save(updatedConversation)
        updateUserConversations(userId, conversationId)

        return savedConversation.toConversationResponse()
    }

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
        updateUserConversations(userId, conversationId, add = false)

        return savedConversation.toConversationResponse()
    }

    fun isUserParticipant(conversationId: String, userId: String): Boolean {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: return false
        return conversation.participantIds.contains(userId)
    }

    fun getConversationParticipants(conversationId: String): List<UserInfo> {
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: throw IllegalArgumentException("Conversation not found")

        return userModuleApi.findUserInfosByIds(conversation.participantIds)
    }
}

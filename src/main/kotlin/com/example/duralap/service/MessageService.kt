package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Message
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.database.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository
) {

    /**
     * Send a new message
     */
    fun sendMessage(request: MessageCreateRequest): MessageResponse {
        // Validate conversation exists
        if (!conversationRepository.existsById(request.conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        // Validate sender exists
        if (!userRepository.existsById(request.senderId)) {
            throw IllegalArgumentException("Sender does not exist")
        }

        // Check if sender is a participant in the conversation
        val conversation = conversationRepository.findByIdOrNull(request.conversationId)
        if (conversation?.participantIds?.contains(request.senderId) == false) {
            throw IllegalArgumentException("Sender is not a participant in this conversation")
        }

        val message = Message(
            id = UUID.randomUUID().toString(),
            conversationId = request.conversationId,
            senderId = request.senderId,
            content = request.content,
            messageType = request.messageType,
            mediaUrl = request.mediaUrl,
            mediaType = request.mediaType,
            fileName = request.fileName,
            fileSize = request.fileSize,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedMessage = messageRepository.save(message)
        return savedMessage.toMessageResponse()
    }

    /**
     * Get messages for a conversation
     */
    fun getMessages(conversationId: String, page: Int = 0, size: Int = 20): List<MessageResponse> {
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        val pageable: Pageable = PageRequest.of(page, size)
        val messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable).content

        return messages.reversed().map { it.toMessageResponse() }
    }

    /**
     * Get all messages for a conversation (without pagination)
     */
    fun getAllMessages(conversationId: String): List<MessageResponse> {
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

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

        // Verify user is a participant in the conversation
        val conversation = conversationRepository.findByIdOrNull(message.conversationId)
        if (conversation?.participantIds?.contains(userId) == false) {
            throw IllegalArgumentException("User is not a participant in this conversation")
        }

        // Only mark as read if the message is not sent by the user themselves
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
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

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
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        return messageRepository.countUnreadMessages(conversationId, userId)
    }

    /**
     * Get unread messages for a user in a conversation
     */
    fun getUnreadMessages(conversationId: String, userId: String): List<MessageResponse> {
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        val messages = messageRepository.findUnreadMessages(conversationId, userId)
        return messages.map { it.toMessageResponse() }
    }

    /**
     * Get last message in a conversation
     */
    fun getLastMessage(conversationId: String): MessageResponse? {
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        val message = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversationId)
        return message?.toMessageResponse()
    }

    /**
     * Delete message
     */
    fun deleteMessage(id: String, userId: String): Boolean {
        val message = messageRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Message not found")

        // Only allow deletion if the user is the sender
        if (message.senderId != userId) {
            throw IllegalArgumentException("Only sender can delete the message")
        }

        messageRepository.deleteById(id)
        return true
    }

    /**
     * Get messages by type
     */
    fun getMessagesByType(conversationId: String, messageType: com.example.duralap.database.model.MessageType): List<MessageResponse> {
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        val messages = messageRepository.findByMessageType(messageType)
            .filter { it.conversationId == conversationId }
            .sortedByDescending { it.createdAt }

        return messages.map { it.toMessageResponse() }
    }

    /**
     * Get messages with media in a conversation
     */
    fun getMediaMessages(conversationId: String): List<MessageResponse> {
        if (!conversationRepository.existsById(conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        val messages = messageRepository.findMessagesWithMedia()
            .filter { it.conversationId == conversationId }
            .sortedByDescending { it.createdAt }

        return messages.map { it.toMessageResponse() }
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
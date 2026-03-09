package com.example.duralap.database.repository

import com.example.duralap.database.model.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : MongoRepository<Message, String> {

    /**
     * Find messages by conversation ID
     */
    fun findByConversationIdOrderByCreatedAtDesc(conversationId: String): List<Message>

    /**
     * Find messages by conversation ID with pagination
     */
    fun findByConversationIdOrderByCreatedAtDesc(conversationId: String, pageable: Pageable): Page<Message>

    /**
     * Find messages by sender ID
     */
    fun findBySenderId(senderId: String): List<Message>

    /**
     * Find unread messages for a user in a conversation
     */
    @Query("{ 'conversationId': ?0, 'senderId': { \$ne: ?1 }, 'isRead': false }")
    fun findUnreadMessages(conversationId: String, userId: String): List<Message>

    /**
     * Count unread messages for a user in a conversation
     */
    @Query("{ 'conversationId': ?0, 'senderId': { \$ne: ?1 }, 'isRead': false }")
    fun countUnreadMessages(conversationId: String, userId: String): Long

    /**
     * Find last message in a conversation
     */
    @Query("{ 'conversationId': ?0 }")
    fun findFirstByConversationIdOrderByCreatedAtDesc(conversationId: String): Message?

    /**
     * Mark messages as read
     */
    @Query("{ 'conversationId': ?0, 'senderId': { \$ne: ?1 }, 'isRead': false }")
    fun markMessagesAsRead(conversationId: String, userId: String): List<Message>

    /**
     * Find messages by conversation and type
     */
    fun findByConversationIdAndMessageType(conversationId: String, messageType: com.example.duralap.database.model.MessageType): List<Message>

    /**
     * Find messages with media for a conversation
     */
    @Query("{ 'conversationId': ?0, 'mediaUrl': { \$exists: true, \$ne: null } }")
    fun findByConversationIdAndMediaUrlIsNotNull(conversationId: String): List<Message>

    /**
     * Find messages created after a specific time
     */
    fun findByCreatedAtAfter(createdAt: java.time.Instant): List<Message>

    /**
     * Find messages by conversation ID and created after a specific time
     */
    @Query("{ 'conversationId': ?0, 'createdAt': { \$gt: ?1 } }")
    fun findByConversationIdAndCreatedAtAfter(conversationId: String, createdAt: java.time.Instant): List<Message>
}
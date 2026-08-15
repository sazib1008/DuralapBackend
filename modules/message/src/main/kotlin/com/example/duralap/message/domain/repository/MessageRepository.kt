package com.example.duralap.message.domain.repository

import com.example.duralap.database.model.MessageType
import com.example.duralap.message.domain.model.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface MessageRepository : MongoRepository<Message, String> {

    fun findByConversationIdOrderByCreatedAtDesc(conversationId: String, pageable: Pageable): Page<Message>

    fun findByConversationIdOrderByCreatedAtDesc(conversationId: String): List<Message>

    fun findFirstByConversationIdOrderByCreatedAtDesc(conversationId: String): Message?

    fun findByConversationIdInAndUpdatedAtAfterOrderByCreatedAtAsc(conversationIds: List<String>, since: Instant): List<Message>

    fun findByConversationIdInAndCreatedAtAfterOrderByCreatedAtAsc(conversationIds: List<String>, since: Instant): List<Message>

    fun findByClientMsgId(clientMsgId: String): Message?

    @Query("{'conversationId': ?0, 'senderId': {'\$ne': ?1}, 'isRead': false}")
    fun countUnreadMessages(conversationId: String, userId: String): Long

    @Query("{'conversationId': ?0, 'senderId': {'\$ne': ?1}, 'isRead': false}")
    fun findUnreadMessages(conversationId: String, userId: String): List<Message>

    @Query("{'conversationId': ?0, 'senderId': {'\$ne': ?1}, 'isRead': false}")
    fun markMessagesAsRead(conversationId: String, userId: String): List<Message>

    fun findByConversationIdAndMessageType(conversationId: String, messageType: MessageType): List<Message>

    fun findByConversationIdAndMediaUrlIsNotNull(conversationId: String): List<Message>

    fun deleteByConversationId(conversationId: String): Long
}

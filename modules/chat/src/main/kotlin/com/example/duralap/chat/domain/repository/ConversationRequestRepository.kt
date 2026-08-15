package com.example.duralap.chat.domain.repository

import com.example.duralap.chat.domain.model.ConversationRequest
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ConversationRequestRepository : MongoRepository<ConversationRequest, String> {

    @Query("{'recipientId': ?0, 'status': 'PENDING'}")
    fun findPendingRequestsForRecipient(recipientId: String): List<ConversationRequest>

    @Query("{'recipientId': ?0, 'status': 'PENDING'}", count = true)
    fun countPendingRequestsForRecipient(recipientId: String): Long

    @Query("{'senderId': ?0, 'recipientId': ?1, 'status': 'PENDING'}")
    fun findPendingRequest(senderId: String, recipientId: String): Optional<ConversationRequest>

    fun findByConversationId(conversationId: String): Optional<ConversationRequest>
}

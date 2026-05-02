package com.example.duralap.database.repository

import com.example.duralap.database.model.ConversationRequest
import com.example.duralap.database.model.ConversationStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ConversationRequestRepository : MongoRepository<ConversationRequest, String> {

    /**
     * Find pending conversation request between two users
     */
    @Query("{ 'senderId': ?0, 'recipientId': ?1, 'status': 'PENDING' }")
    fun findPendingRequest(senderId: String, recipientId: String): Optional<ConversationRequest>

    /**
     * Find all pending requests for a user (received)
     */
    @Query("{ 'recipientId': ?0, 'status': 'PENDING' }")
    fun findPendingRequestsForRecipient(recipientId: String): List<ConversationRequest>

    /**
     * Find all requests sent by a user
     */
    @Query("{ 'senderId': ?0, 'status': ?1 }")
    fun findRequestsBySender(senderId: String, status: ConversationStatus): List<ConversationRequest>

    /**
     * Find conversation request by conversation ID
     */
    fun findByConversationId(conversationId: String): Optional<ConversationRequest>

    /**
     * Find accepted/rejected requests for a user
     */
    @Query("{ '\$or': [ { 'senderId': ?0 }, { 'recipientId': ?0 } ], 'status': { \$in: ?1 } }")
    fun findRequestsByUserAndStatuses(userId: String, statuses: List<ConversationStatus>): List<ConversationRequest>

    /**
     * Count pending requests for a user
     */
    @Query(value = "{ 'recipientId': ?0, 'status': 'PENDING' }", count = true)
    fun countPendingRequestsForRecipient(recipientId: String): Long

    /**
     * Check if there's any existing request between two users
     */
    @Query("{ '\$or': [ { 'senderId': ?0, 'recipientId': ?1 }, { 'senderId': ?1, 'recipientId': ?0 } ] }")
    fun findAnyRequestBetweenUsers(user1Id: String, user2Id: String): List<ConversationRequest>
}

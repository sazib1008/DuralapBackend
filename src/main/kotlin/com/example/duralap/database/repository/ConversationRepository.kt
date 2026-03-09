package com.example.duralap.database.repository

import com.example.duralap.database.model.Conversation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ConversationRepository : MongoRepository<Conversation, String> {

    /**
     * Find conversation by participant IDs (exact match)
     */
    @Query("{ 'participantIds': { \$all: ?0, \$size: ?0 } }")
    fun findByParticipantIds(participantIds: Set<String>): Optional<Conversation>

    /**
     * Find conversations containing specific participant
     */
    fun findByParticipantIdsContaining(userId: String): List<Conversation>

    /**
     * Find conversation between two specific users
     */
    @Query("{ 'participantIds': { \$all: [?0, ?1], \$size: 2 } }")
    fun findByParticipantIdsContainingAndParticipantIdsContaining(user1: String, user2: String): Optional<Conversation>

    /**
     * Find conversations for a user with last message and unread count
     */
    @Query("""
        {
            'participantIds': ?0
        }
    """)
    fun findConversationsByUserId(userId: String): List<Conversation>

    /**
     * Check if conversation exists between participants
     */
    @Query("{ 'participantIds': { \$all: ?0, \$size: ?0 } }")
    fun existsByParticipantIds(participantIds: Set<String>): Boolean

    /**
     * Find conversations updated after a specific time
     */
    fun findByCreatedAtAfter(createdAt: java.time.Instant): List<Conversation>
}
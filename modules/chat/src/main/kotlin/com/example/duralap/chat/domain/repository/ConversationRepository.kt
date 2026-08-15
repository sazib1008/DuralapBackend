package com.example.duralap.chat.domain.repository

import com.example.duralap.chat.domain.model.Conversation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepository : MongoRepository<Conversation, String> {

    @Query("{'participantIds': {'\$all': ?0}, 'participantIds': {'\$size': ?1}}")
    fun findByParticipantIds(participantIds: Set<String>, size: Int): List<Conversation>

    fun findByParticipantIdsContaining(userId: String): List<Conversation>

    @Query("{'participantIds': {'\$all': [?0, ?1]}}")
    fun findByParticipantIdsContainingAndParticipantIdsContaining(user1Id: String, user2Id: String): List<Conversation>
}

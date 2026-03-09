package com.example.duralap.service.cache

import com.example.duralap.database.repository.ConversationRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ConversationValidationCache(
    private val conversationRepository: ConversationRepository,
    private val redisTemplate: StringRedisTemplate
) {

    fun verifyUserParticipantInConversation(userId: String, conversationId: String) {
        val cacheKey = "conversation:$conversationId:participants"
        
        // Try getting from cache first
        val isMember = redisTemplate.opsForSet().isMember(cacheKey, userId)
        if (isMember == true) {
            return
        }

        // Fallback to DB
        val conversation = conversationRepository.findByIdOrNull(conversationId)
            ?: throw IllegalArgumentException("Conversation does not exist")

        if (!conversation.participantIds.contains(userId)) {
            throw IllegalArgumentException("User is not a participant in this conversation")
        }

        // Cache for 1 hour to prevent constant DB lookups
        conversation.participantIds.forEach { 
            redisTemplate.opsForSet().add(cacheKey, it)
        }
        redisTemplate.expire(cacheKey, Duration.ofHours(1))
    }
}

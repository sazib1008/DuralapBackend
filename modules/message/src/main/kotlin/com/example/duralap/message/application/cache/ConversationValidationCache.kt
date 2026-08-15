package com.example.duralap.message.application.cache

import com.example.duralap.chat.api.ChatModuleApi
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ConversationValidationCache(
    private val chatModuleApi: ChatModuleApi,
    private val redisTemplate: StringRedisTemplate
) {

    fun verifyUserParticipantInConversation(userId: String, conversationId: String) {
        val cacheKey = "conversation:$conversationId:participants"
        
        val isMember = redisTemplate.opsForSet().isMember(cacheKey, userId)
        if (isMember == true) {
            return
        }

        val participantIds = chatModuleApi.getParticipantIds(conversationId)
        if (participantIds.isEmpty()) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        if (!participantIds.contains(userId)) {
            throw IllegalArgumentException("User is not a participant in this conversation")
        }

        participantIds.forEach { 
            redisTemplate.opsForSet().add(cacheKey, it)
        }
        redisTemplate.expire(cacheKey, Duration.ofHours(1))
    }
}

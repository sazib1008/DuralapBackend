package com.example.duralap.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * Rate limiting service using Redis to prevent API abuse and spam.
 * Implements sliding window rate limiting for various operations.
 */
@Service
class RateLimitingService(
    private val redisTemplate: StringRedisTemplate
) {
    /**
     * Check if an action is allowed based on rate limits
     * 
     * @param key Unique identifier for the rate limit (e.g., "message:user123")
     * @param maxRequests Maximum number of requests allowed
     * @param window Duration of the rate limit window
     * @return true if action is allowed, false if rate limited
     */
    fun isAllowed(key: String, maxRequests: Int, window: Duration): Boolean {
        val redisKey = "ratelimit:$key"
        val now = System.currentTimeMillis()
        val windowStart = now - window.toMillis()

        // Remove old entries outside the window
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0.0, windowStart.toDouble())

        // Count current requests in window
        val currentCount = redisTemplate.opsForZSet().zCard(redisKey) ?: 0

        if (currentCount >= maxRequests) {
            return false
        }

        // Add current request
        redisTemplate.opsForZSet().add(redisKey, now.toString(), now.toDouble())
        
        // Set expiry on the key
        redisTemplate.expire(redisKey, window)

        return true
    }

    /**
     * Rate limit for sending messages (e.g., 60 messages per minute)
     */
    fun canSendMessage(userId: String): Boolean {
        return isAllowed("message:$userId", 60, Duration.ofMinutes(1))
    }

    /**
     * Rate limit for creating conversation requests (e.g., 10 per hour)
     */
    fun canCreateConversationRequest(userId: String): Boolean {
        return isAllowed("conversation_request:$userId", 10, Duration.ofHours(1))
    }

    /**
     * Rate limit for user search (e.g., 30 searches per minute)
     */
    fun canSearchUsers(userId: String): Boolean {
        return isAllowed("search:$userId", 30, Duration.ofMinutes(1))
    }

    /**
     * Rate limit for authentication attempts (e.g., 50 per 15 minutes for testing)
     */
    fun canAttemptLogin(ipAddress: String): Boolean {
        return isAllowed("login:$ipAddress", 50, Duration.ofMinutes(15))
    }

    /**
     * Rate limit for file uploads (e.g., 20 per hour)
     */
    fun canUploadFile(userId: String): Boolean {
        return isAllowed("upload:$userId", 20, Duration.ofHours(1))
    }

    /**
     * Get remaining requests for a rate limit key
     */
    fun getRemainingRequests(key: String, maxRequests: Int, window: Duration): Int {
        val redisKey = "ratelimit:$key"
        val now = System.currentTimeMillis()
        val windowStart = now - window.toMillis()

        // Remove old entries
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0.0, windowStart.toDouble())

        // Count current requests
        val currentCount = redisTemplate.opsForZSet().zCard(redisKey) ?: 0

        return maxOf(0, maxRequests - currentCount.toInt())
    }
}

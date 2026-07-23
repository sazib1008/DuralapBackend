package com.example.duralap.service.cache

import com.example.duralap.database.dto.UserResponse
import com.example.duralap.database.model.User
import com.example.duralap.database.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Duration

/**
 * Optimized caching layer for user data to reduce database queries.
 * Uses Redis for fast lookups of frequently accessed user profiles.
 */
@Service
class UserCache(
    private val userRepository: UserRepository,
    private val redisTemplate: StringRedisTemplate
) {
    private val objectMapper = jacksonObjectMapper()
    private val CACHE_PREFIX = "user:cache:"
    private val CACHE_TTL = Duration.ofMinutes(30)

    /**
     * Get user by ID with caching
     */
    fun getUserById(userId: String): User? {
        val cacheKey = "$CACHE_PREFIX$userId"
        
        // Try cache first
        val cachedUser = redisTemplate.opsForValue().get(cacheKey)
        if (cachedUser != null) {
            return try {
                objectMapper.readValue(cachedUser, User::class.java)
            } catch (e: Exception) {
                null
            }
        }

        // Fallback to DB
        val user = userRepository.findByIdOrNull(userId) ?: return null

        // Cache the result
        cacheUser(user)

        return user
    }

    /**
     * Get user response by ID with caching
     */
    fun getUserResponseById(userId: String): UserResponse? {
        return getUserById(userId)?.toUserResponse()
    }

    /**
     * Get user by username with caching
     */
    fun getUserByUsername(username: String): User? {
        val cacheKey = "$CACHE_PREFIX$username:by-username"
        
        // Try cache first
        val cachedUser = redisTemplate.opsForValue().get(cacheKey)
        if (cachedUser != null) {
            return try {
                objectMapper.readValue(cachedUser, User::class.java)
            } catch (e: Exception) {
                null
            }
        }

        // Fallback to DB
        val user = userRepository.findByUsername(username).orElse(null) ?: return null

        // Cache the result
        cacheUserByUsername(user, username)

        return user
    }

    /**
     * Cache user by ID
     */
    fun cacheUser(user: User) {
        val cacheKey = "$CACHE_PREFIX${user.id}"
        try {
            val userJson = objectMapper.writeValueAsString(user)
            redisTemplate.opsForValue().set(cacheKey, userJson, CACHE_TTL)
        } catch (e: Exception) {
            // Log but don't fail if caching fails
        }
    }

    /**
     * Cache user by username
     */
    fun cacheUserByUsername(user: User, username: String) {
        val cacheKey = "$CACHE_PREFIX$username:by-username"
        try {
            val userJson = objectMapper.writeValueAsString(user)
            redisTemplate.opsForValue().set(cacheKey, userJson, CACHE_TTL)
        } catch (e: Exception) {
            // Log but don't fail if caching fails
        }
    }

    /**
     * Invalidate user cache (use after updates)
     */
    fun invalidateUserCache(userId: String, username: String? = null) {
        redisTemplate.delete("$CACHE_PREFIX$userId")
        if (username != null) {
            redisTemplate.delete("$CACHE_PREFIX$username:by-username")
        }
    }

    /**
     * Batch get users by IDs (optimized with cache)
     */
    fun getUsersByIds(userIds: Set<String>): Map<String, User> {
        val result = mutableMapOf<String, User>()
        val missingIds = mutableSetOf<String>()

        // Try to get from cache first
        userIds.forEach { userId ->
            val cachedUser = redisTemplate.opsForValue().get("$CACHE_PREFIX$userId")
            if (cachedUser != null) {
                try {
                    val user = objectMapper.readValue(cachedUser, User::class.java)
                    result[userId] = user
                } catch (e: Exception) {
                    missingIds.add(userId)
                }
            } else {
                missingIds.add(userId)
            }
        }

        // Fetch missing from DB
        if (missingIds.isNotEmpty()) {
            val users = userRepository.findAllById(missingIds)
            users.forEach { user ->
                result[user.id!!] = user
                cacheUser(user)
            }
        }

        return result
    }
}

/**
 * Extension function to convert User to UserResponse
 */
fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id ?: throw IllegalStateException("User ID cannot be null"),
        username = this.username,
        email = this.email,
        fullName = this.fullName,
        bio = this.bio,
        profileImageUrl = this.profileImageUrl,
        phoneNumber = this.phoneNumber,
        isVerified = this.isVerified,
        status = this.status,
        lastSeen = this.lastSeen,
        isInCall = this.isInCall,
        currentCallId = this.currentCallId,
        roles = this.roles,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

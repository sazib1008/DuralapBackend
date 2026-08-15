package com.example.duralap.user.application.cache

import com.example.duralap.database.dto.UserResponse
import com.example.duralap.user.domain.model.User
import com.example.duralap.user.domain.model.toUserResponse
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Duration

@Service
class UserCache(
    private val userRepository: UserRepository,
    private val redisTemplate: StringRedisTemplate
) {
    private val objectMapper = jacksonObjectMapper()
    private val CACHE_PREFIX = "user:cache:"
    private val CACHE_TTL = Duration.ofMinutes(30)

    fun getUserById(userId: String): User? {
        val cacheKey = "$CACHE_PREFIX$userId"
        val cachedUser = redisTemplate.opsForValue().get(cacheKey)
        if (cachedUser != null) {
            return try {
                objectMapper.readValue(cachedUser, User::class.java)
            } catch (e: Exception) {
                null
            }
        }

        val user = userRepository.findByIdOrNull(userId) ?: return null
        cacheUser(user)
        return user
    }

    fun getUserResponseById(userId: String): UserResponse? {
        return getUserById(userId)?.toUserResponse()
    }

    fun getUserByUsername(username: String): User? {
        val cacheKey = "$CACHE_PREFIX$username:by-username"
        val cachedUser = redisTemplate.opsForValue().get(cacheKey)
        if (cachedUser != null) {
            return try {
                objectMapper.readValue(cachedUser, User::class.java)
            } catch (e: Exception) {
                null
            }
        }

        val user = userRepository.findByUsername(username).orElse(null) ?: return null
        cacheUserByUsername(user, username)
        return user
    }

    fun cacheUser(user: User) {
        val cacheKey = "$CACHE_PREFIX${user.id}"
        try {
            val userJson = objectMapper.writeValueAsString(user)
            redisTemplate.opsForValue().set(cacheKey, userJson, CACHE_TTL)
        } catch (e: Exception) {
            // Non-blocking cache logging
        }
    }

    fun cacheUserByUsername(user: User, username: String) {
        val cacheKey = "$CACHE_PREFIX$username:by-username"
        try {
            val userJson = objectMapper.writeValueAsString(user)
            redisTemplate.opsForValue().set(cacheKey, userJson, CACHE_TTL)
        } catch (e: Exception) {
            // Non-blocking cache logging
        }
    }

    fun invalidateUserCache(userId: String, username: String? = null) {
        redisTemplate.delete("$CACHE_PREFIX$userId")
        if (username != null) {
            redisTemplate.delete("$CACHE_PREFIX$username:by-username")
        }
    }

    fun getUsersByIds(userIds: Set<String>): Map<String, User> {
        val result = mutableMapOf<String, User>()
        val missingIds = mutableSetOf<String>()

        userIds.forEach { userId ->
            val cachedUser = redisTemplate.opsForValue().get("$CACHE_PREFIX$userId")
            if (cachedUser != null) {
                try {
                    result[userId] = objectMapper.readValue(cachedUser, User::class.java)
                } catch (e: Exception) {
                    missingIds.add(userId)
                }
            } else {
                missingIds.add(userId)
            }
        }

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

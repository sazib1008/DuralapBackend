package com.example.duralap.presence.application.cache

import com.example.duralap.database.dto.UserPresenceResponse
import com.example.duralap.database.model.UserStatus
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class UserPresenceCache(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(UserPresenceCache::class.java)

    companion object {
        const val DEFAULT_SESSION_TTL_SECONDS = 45L
        const val USER_SESSIONS_PREFIX = "presence:user:"
        const val SESSION_DATA_PREFIX = "presence:session:"
        const val ALL_ACTIVE_USERS_KEY = "presence:active_users"
    }

    private val registerScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local userKey = KEYS[1]
            local sessionKey = KEYS[2]
            local allUsersKey = KEYS[3]
            local userId = ARGV[1]
            local sessionId = ARGV[2]
            local sessionData = ARGV[3]
            local ttl = tonumber(ARGV[4])

            -- Count active sessions before adding
            local oldSessions = redis.call('SMEMBERS', userKey)
            local activeBefore = 0
            for _, sId in ipairs(oldSessions) do
                if redis.call('EXISTS', 'presence:session:' .. sId) == 1 then
                    activeBefore = activeBefore + 1
                else
                    redis.call('SREM', userKey, sId)
                end
            end

            -- Register new session
            redis.call('SADD', userKey, sessionId)
            redis.call('SET', sessionKey, sessionData, 'EX', ttl)
            redis.call('SADD', allUsersKey, userId)

            return activeBefore
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    private val removeScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local userKey = KEYS[1]
            local sessionKey = KEYS[2]
            local allUsersKey = KEYS[3]
            local userId = ARGV[1]
            local sessionId = ARGV[2]

            redis.call('DEL', sessionKey)
            redis.call('SREM', userKey, sessionId)

            local sessions = redis.call('SMEMBERS', userKey)
            local activeRemaining = 0
            for _, sId in ipairs(sessions) do
                if redis.call('EXISTS', 'presence:session:' .. sId) == 1 then
                    activeRemaining = activeRemaining + 1
                else
                    redis.call('SREM', userKey, sId)
                end
            end

            if activeRemaining == 0 then
                redis.call('DEL', userKey)
                redis.call('SREM', allUsersKey, userId)
            end

            return activeRemaining
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    private val heartbeatScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local userKey = KEYS[1]
            local sessionKey = KEYS[2]
            local allUsersKey = KEYS[3]
            local userId = ARGV[1]
            local sessionId = ARGV[2]
            local ttl = tonumber(ARGV[3])

            if redis.call('EXISTS', sessionKey) == 1 then
                redis.call('EXPIRE', sessionKey, ttl)
                redis.call('SADD', userKey, sessionId)
                redis.call('SADD', allUsersKey, userId)
                return 1
            else
                return 0
            end
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    private val countActiveScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local userKey = KEYS[1]
            local allUsersKey = KEYS[2]
            local userId = ARGV[1]

            local sessions = redis.call('SMEMBERS', userKey)
            local activeCount = 0
            for _, sId in ipairs(sessions) do
                if redis.call('EXISTS', 'presence:session:' .. sId) == 1 then
                    activeCount = activeCount + 1
                else
                    redis.call('SREM', userKey, sId)
                end
            end

            if activeCount == 0 then
                redis.call('DEL', userKey)
                redis.call('SREM', allUsersKey, userId)
            end

            return activeCount
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    /**
     * Registers a session for a user.
     * Returns the number of active sessions BEFORE this registration.
     * If 0 -> represents an OFFLINE -> ONLINE transition.
     */
    fun registerSession(
        userId: String,
        sessionId: String,
        deviceId: String? = null,
        clientType: String? = "ANDROID",
        ttlSeconds: Long = DEFAULT_SESSION_TTL_SECONDS
    ): Long {
        val userKey = "$USER_SESSIONS_PREFIX$userId:sessions"
        val sessionKey = "$SESSION_DATA_PREFIX$sessionId"
        val sessionData = objectMapper.writeValueAsString(
            mapOf(
                "userId" to userId,
                "sessionId" to sessionId,
                "deviceId" to (deviceId ?: "unknown"),
                "clientType" to (clientType ?: "ANDROID"),
                "connectedAt" to Instant.now().toString(),
                "lastHeartbeat" to Instant.now().toString()
            )
        )

        return redisTemplate.execute(
            registerScript,
            listOf(userKey, sessionKey, ALL_ACTIVE_USERS_KEY),
            userId,
            sessionId,
            sessionData,
            ttlSeconds.toString()
        ) ?: 0L
    }

    /**
     * Refreshes the session TTL without MongoDB/event overhead.
     * Returns true if session exists and was refreshed; false if session expired.
     */
    fun heartbeatSession(
        userId: String,
        sessionId: String,
        ttlSeconds: Long = DEFAULT_SESSION_TTL_SECONDS
    ): Boolean {
        val userKey = "$USER_SESSIONS_PREFIX$userId:sessions"
        val sessionKey = "$SESSION_DATA_PREFIX$sessionId"

        val result = redisTemplate.execute(
            heartbeatScript,
            listOf(userKey, sessionKey, ALL_ACTIVE_USERS_KEY),
            userId,
            sessionId,
            ttlSeconds.toString()
        ) ?: 0L

        return result == 1L
    }

    /**
     * Removes a session explicitly (e.g. STOMP DISCONNECT or socket close).
     * Returns the remaining active session count.
     * If 0 -> represents an ONLINE -> OFFLINE transition.
     */
    fun removeSession(userId: String, sessionId: String): Long {
        val userKey = "$USER_SESSIONS_PREFIX$userId:sessions"
        val sessionKey = "$SESSION_DATA_PREFIX$sessionId"

        return redisTemplate.execute(
            removeScript,
            listOf(userKey, sessionKey, ALL_ACTIVE_USERS_KEY),
            userId,
            sessionId
        ) ?: 0L
    }

    /**
     * Returns the active session count for a user (prunes expired session IDs).
     */
    fun getActiveSessionCount(userId: String): Int {
        val userKey = "$USER_SESSIONS_PREFIX$userId:sessions"
        val count = redisTemplate.execute(
            countActiveScript,
            listOf(userKey, ALL_ACTIVE_USERS_KEY),
            userId
        ) ?: 0L
        return count.toInt()
    }

    /**
     * Checks if a user is online (session count > 0).
     */
    fun isUserOnline(userId: String): Boolean {
        return getActiveSessionCount(userId) > 0
    }

    /**
     * Returns the presence response for a single user.
     */
    fun getUserPresence(userId: String): UserPresenceResponse {
        val count = getActiveSessionCount(userId)
        val status = if (count > 0) UserStatus.ONLINE else UserStatus.OFFLINE
        return UserPresenceResponse(
            userId = userId,
            status = status,
            sessionCount = count,
            timestamp = Instant.now()
        )
    }

    /**
     * Returns presence responses in batch for multiple user IDs.
     */
    fun getUsersPresence(userIds: Collection<String>): Map<String, UserPresenceResponse> {
        if (userIds.isEmpty()) return emptyMap()
        return userIds.associateWith { userId ->
            getUserPresence(userId)
        }
    }

    /**
     * Returns all currently tracked user IDs.
     */
    fun getAllTrackedUserIds(): Set<String> {
        return redisTemplate.opsForSet().members(ALL_ACTIVE_USERS_KEY) ?: emptySet()
    }

    /**
     * Backward-compatible simple online setter (registers default session).
     */
    fun setUserOnline(userId: String) {
        registerSession(userId, "default-$userId")
    }

    /**
     * Backward-compatible simple offline setter (removes default session and cleans all).
     */
    fun setUserOffline(userId: String) {
        val userKey = "$USER_SESSIONS_PREFIX$userId:sessions"
        val sessions = redisTemplate.opsForSet().members(userKey) ?: emptySet()
        sessions.forEach { sId ->
            redisTemplate.delete("$SESSION_DATA_PREFIX$sId")
        }
        redisTemplate.delete(userKey)
        redisTemplate.opsForSet().remove(ALL_ACTIVE_USERS_KEY, userId)
    }
}

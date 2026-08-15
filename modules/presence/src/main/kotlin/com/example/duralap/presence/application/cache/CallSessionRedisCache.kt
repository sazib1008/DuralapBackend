package com.example.duralap.presence.application.cache

import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import java.time.Instant

data class CallSessionData(
    val callId: String,
    val conversationId: String,
    val callerId: String,
    val calleeId: String,
    val callType: CallType,
    val status: CallStatus,
    val createdAt: String,
    val updatedAt: String
)

enum class CallAcquireStatus {
    SUCCESS,
    CALLER_BUSY,
    CALLEE_BUSY
}

data class CallAcquireResult(
    val status: CallAcquireStatus,
    val existingCallId: String? = null
)

@Service
class CallSessionRedisCache(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(CallSessionRedisCache::class.java)

    companion object {
        const val CALL_SESSION_PREFIX = "call:session:"
        const val USER_ACTIVE_CALL_PREFIX = "user:active_call:"
        const val DEFAULT_RINGING_TTL = 45L // 45 seconds ringing timeout
        const val DEFAULT_ACTIVE_CALL_TTL = 3600L // 1 hour active call TTL
    }

    private val acquireCallScript = DefaultRedisScript<String>().apply {
        setScriptText(
            """
            local callerLockKey = KEYS[1]
            local calleeLockKey = KEYS[2]
            local sessionKey = KEYS[3]
            local callId = ARGV[1]
            local sessionData = ARGV[2]
            local ttl = tonumber(ARGV[3])

            local callerActive = redis.call('GET', callerLockKey)
            if callerActive and callerActive ~= "" and callerActive ~= callId then
                return "CALLER_BUSY:" .. callerActive
            end

            local calleeActive = redis.call('GET', calleeLockKey)
            if calleeActive and calleeActive ~= "" and calleeActive ~= callId then
                return "CALLEE_BUSY:" .. calleeActive
            end

            redis.call('SET', callerLockKey, callId, 'EX', ttl)
            redis.call('SET', calleeLockKey, callId, 'EX', ttl)
            redis.call('SET', sessionKey, sessionData, 'EX', ttl)

            return "SUCCESS"
            """.trimIndent()
        )
        resultType = String::class.java
    }

    private val releaseCallScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local callerLockKey = KEYS[1]
            local calleeLockKey = KEYS[2]
            local sessionKey = KEYS[3]
            local callId = ARGV[1]

            local callerActive = redis.call('GET', callerLockKey)
            if callerActive == callId then
                redis.call('DEL', callerLockKey)
            end

            local calleeActive = redis.call('GET', calleeLockKey)
            if calleeActive == callId then
                redis.call('DEL', calleeLockKey)
            end

            redis.call('DEL', sessionKey)
            return 1
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    private val extendSessionScript = DefaultRedisScript<Long>().apply {
        setScriptText(
            """
            local callerLockKey = KEYS[1]
            local calleeLockKey = KEYS[2]
            local sessionKey = KEYS[3]
            local callId = ARGV[1]
            local ttl = tonumber(ARGV[2])

            if redis.call('EXISTS', sessionKey) == 1 then
                redis.call('EXPIRE', sessionKey, ttl)
                if redis.call('GET', callerLockKey) == callId then
                    redis.call('EXPIRE', callerLockKey, ttl)
                end
                if redis.call('GET', calleeLockKey) == callId then
                    redis.call('EXPIRE', calleeLockKey, ttl)
                end
                return 1
            else
                return 0
            end
            """.trimIndent()
        )
        resultType = Long::class.java
    }

    /**
     * Atomically tries to acquire a call session for caller and callee.
     * Prevents simultaneous call collision.
     */
    fun tryAcquireCall(
        callId: String,
        callerId: String,
        calleeId: String,
        conversationId: String,
        callType: CallType,
        ttlSeconds: Long = DEFAULT_RINGING_TTL
    ): CallAcquireResult {
        val callerLockKey = "$USER_ACTIVE_CALL_PREFIX$callerId"
        val calleeLockKey = "$USER_ACTIVE_CALL_PREFIX$calleeId"
        val sessionKey = "$CALL_SESSION_PREFIX$callId"

        val nowStr = Instant.now().toString()
        val session = CallSessionData(
            callId = callId,
            conversationId = conversationId,
            callerId = callerId,
            calleeId = calleeId,
            callType = callType,
            status = CallStatus.RINGING,
            createdAt = nowStr,
            updatedAt = nowStr
        )
        val sessionData = objectMapper.writeValueAsString(session)

        val result = redisTemplate.execute(
            acquireCallScript,
            listOf(callerLockKey, calleeLockKey, sessionKey),
            callId,
            sessionData,
            ttlSeconds.toString()
        ) ?: "UNKNOWN"

        return when {
            result == "SUCCESS" -> CallAcquireResult(CallAcquireStatus.SUCCESS)
            result.startsWith("CALLER_BUSY:") -> CallAcquireResult(CallAcquireStatus.CALLER_BUSY, result.substringAfter("CALLER_BUSY:"))
            result.startsWith("CALLEE_BUSY:") -> CallAcquireResult(CallAcquireStatus.CALLEE_BUSY, result.substringAfter("CALLEE_BUSY:"))
            else -> CallAcquireResult(CallAcquireStatus.CALLEE_BUSY)
        }
    }

    fun updateCallStatus(callId: String, newStatus: CallStatus, extendTtl: Boolean = false) {
        val sessionKey = "$CALL_SESSION_PREFIX$callId"
        val existingJson = redisTemplate.opsForValue().get(sessionKey) ?: return

        try {
            val existing = objectMapper.readValue(existingJson, CallSessionData::class.java)
            val updated = existing.copy(status = newStatus, updatedAt = Instant.now().toString())
            val updatedJson = objectMapper.writeValueAsString(updated)

            val ttl = if (extendTtl) DEFAULT_ACTIVE_CALL_TTL else DEFAULT_RINGING_TTL
            redisTemplate.opsForValue().set(sessionKey, updatedJson, java.time.Duration.ofSeconds(ttl))

            if (extendTtl) {
                extendCallSession(callId, existing.callerId, existing.calleeId, ttl)
            }
        } catch (e: Exception) {
            logger.error("Failed to update call status in Redis for callId: {}", callId, e)
        }
    }

    fun extendCallSession(
        callId: String,
        callerId: String,
        calleeId: String,
        ttlSeconds: Long = DEFAULT_ACTIVE_CALL_TTL
    ): Boolean {
        val callerLockKey = "$USER_ACTIVE_CALL_PREFIX$callerId"
        val calleeLockKey = "$USER_ACTIVE_CALL_PREFIX$calleeId"
        val sessionKey = "$CALL_SESSION_PREFIX$callId"

        val result = redisTemplate.execute(
            extendSessionScript,
            listOf(callerLockKey, calleeLockKey, sessionKey),
            callId,
            ttlSeconds.toString()
        ) ?: 0L
        return result == 1L
    }

    fun releaseCall(callId: String, callerId: String, calleeId: String) {
        val callerLockKey = "$USER_ACTIVE_CALL_PREFIX$callerId"
        val calleeLockKey = "$USER_ACTIVE_CALL_PREFIX$calleeId"
        val sessionKey = "$CALL_SESSION_PREFIX$callId"

        redisTemplate.execute(
            releaseCallScript,
            listOf(callerLockKey, calleeLockKey, sessionKey),
            callId
        )
        logger.info("Released call session and locks for callId: {} (caller={}, callee={})", callId, callerId, calleeId)
    }

    fun getCallSession(callId: String): CallSessionData? {
        val sessionKey = "$CALL_SESSION_PREFIX$callId"
        val json = redisTemplate.opsForValue().get(sessionKey) ?: return null
        return try {
            objectMapper.readValue(json, CallSessionData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getActiveCallIdForUser(userId: String): String? {
        val key = "$USER_ACTIVE_CALL_PREFIX$userId"
        return redisTemplate.opsForValue().get(key)
    }

    fun isUserInCall(userId: String): Boolean {
        val callId = getActiveCallIdForUser(userId)
        return !callId.isNullOrBlank()
    }
}

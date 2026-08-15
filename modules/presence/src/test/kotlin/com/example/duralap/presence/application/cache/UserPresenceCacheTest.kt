package com.example.duralap.presence.application.cache

import com.example.duralap.database.model.UserStatus
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript

@ExtendWith(MockitoExtension::class)
class UserPresenceCacheTest {

    @Mock
    private lateinit var redisTemplate: StringRedisTemplate

    @Mock
    private lateinit var setOperations: SetOperations<String, String>

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    private lateinit var presenceCache: UserPresenceCache

    @BeforeEach
    fun setUp() {
        presenceCache = UserPresenceCache(redisTemplate, objectMapper)
    }

    @Test
    fun `registerSession should return 0 for first device connecting (offline to online)`() {
        val userId = "user-1"
        val sessionId = "session-1"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId), eq(sessionId), anyString(), eq("45")))
            .thenReturn(0L)

        val activeBefore = presenceCache.registerSession(userId, sessionId, deviceId = "phone-1", ttlSeconds = 45)

        assertThat(activeBefore).isEqualTo(0L)
    }

    @Test
    fun `registerSession should return 1 for second device connecting (already online)`() {
        val userId = "user-1"
        val sessionId = "session-2"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId), eq(sessionId), anyString(), eq("45")))
            .thenReturn(1L)

        val activeBefore = presenceCache.registerSession(userId, sessionId, deviceId = "laptop-1", ttlSeconds = 45)

        assertThat(activeBefore).isEqualTo(1L)
    }

    @Test
    fun `heartbeatSession should return true when session is refreshed in Redis`() {
        val userId = "user-1"
        val sessionId = "session-1"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId), eq(sessionId), eq("45")))
            .thenReturn(1L)

        val refreshed = presenceCache.heartbeatSession(userId, sessionId, ttlSeconds = 45)

        assertThat(refreshed).isTrue()
    }

    @Test
    fun `heartbeatSession should return false when session has expired in Redis`() {
        val userId = "user-1"
        val sessionId = "session-stale"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId), eq(sessionId), eq("45")))
            .thenReturn(0L)

        val refreshed = presenceCache.heartbeatSession(userId, sessionId, ttlSeconds = 45)

        assertThat(refreshed).isFalse()
    }

    @Test
    fun `removeSession should return remaining active sessions count (1 remaining for multi-device)`() {
        val userId = "user-1"
        val sessionId = "session-phone"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId), eq(sessionId)))
            .thenReturn(1L)

        val activeRemaining = presenceCache.removeSession(userId, sessionId)

        assertThat(activeRemaining).isEqualTo(1L)
    }

    @Test
    fun `removeSession should return 0 remaining when last device disconnects (online to offline)`() {
        val userId = "user-1"
        val sessionId = "session-last"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId), eq(sessionId)))
            .thenReturn(0L)

        val activeRemaining = presenceCache.removeSession(userId, sessionId)

        assertThat(activeRemaining).isEqualTo(0L)
    }

    @Test
    fun `getUserPresence should return ONLINE when activeSessionCount is greater than 0`() {
        val userId = "user-1"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId)))
            .thenReturn(2L)

        val presence = presenceCache.getUserPresence(userId)

        assertThat(presence.userId).isEqualTo(userId)
        assertThat(presence.status).isEqualTo(UserStatus.ONLINE)
        assertThat(presence.sessionCount).isEqualTo(2)
    }

    @Test
    fun `getUserPresence should return OFFLINE when activeSessionCount is 0`() {
        val userId = "user-2"

        `when`(redisTemplate.execute(any<RedisScript<Long>>(), anyList(), eq(userId)))
            .thenReturn(0L)

        val presence = presenceCache.getUserPresence(userId)

        assertThat(presence.userId).isEqualTo(userId)
        assertThat(presence.status).isEqualTo(UserStatus.OFFLINE)
        assertThat(presence.sessionCount).isEqualTo(0)
    }
}

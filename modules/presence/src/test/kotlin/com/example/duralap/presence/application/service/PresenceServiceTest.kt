package com.example.duralap.presence.application.service

import com.example.duralap.database.model.UserStatus
import com.example.duralap.events.UserPresenceChangedEvent
import com.example.duralap.presence.application.cache.UserPresenceCache
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockitoExtension::class)
class PresenceServiceTest {

    @Mock
    private lateinit var presenceCache: UserPresenceCache

    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

    private lateinit var presenceService: PresenceService

    @BeforeEach
    fun setUp() {
        presenceService = PresenceService(presenceCache, eventPublisher)
    }

    @Test
    fun `registerSession for first device should emit ONLINE presence event`() {
        val userId = "user-1"
        val sessionId = "session-phone"

        // First device: active sessions before registration was 0
        `when`(presenceCache.registerSession(userId, sessionId, "phone-1", "ANDROID", 45L))
            .thenReturn(0L)

        presenceService.registerSession(userId, sessionId, "phone-1", "ANDROID")

        val eventCaptor = ArgumentCaptor.forClass(UserPresenceChangedEvent::class.java)
        verify(eventPublisher).publishEvent(eventCaptor.capture())

        val event = eventCaptor.value
        assertThat(event.userId).isEqualTo(userId)
        assertThat(event.status).isEqualTo(UserStatus.ONLINE)
        assertThat(event.sessionCount).isEqualTo(1)
    }

    @Test
    fun `registerSession for second device (multi-device) should NOT emit duplicate ONLINE event`() {
        val userId = "user-1"
        val session1 = "session-phone"
        val session2 = "session-laptop"

        // First device
        `when`(presenceCache.registerSession(userId, session1, "phone-1", "ANDROID", 45L))
            .thenReturn(0L)
        presenceService.registerSession(userId, session1, "phone-1", "ANDROID")

        // Second device: active sessions before was already 1
        `when`(presenceCache.registerSession(userId, session2, "laptop-1", "WEB", 45L))
            .thenReturn(1L)
        presenceService.registerSession(userId, session2, "laptop-1", "WEB")

        // Verify only 1 event was published (for the first session)
        verify(eventPublisher, times(1)).publishEvent(any(UserPresenceChangedEvent::class.java))
    }

    @Test
    fun `removeSession for one device when other devices are active should NOT emit OFFLINE event`() {
        val userId = "user-1"
        val sessionPhone = "session-phone"

        // Disconnecting phone leaves 1 active session (laptop)
        `when`(presenceCache.removeSession(userId, sessionPhone))
            .thenReturn(1L)

        presenceService.removeSession(userId, sessionPhone)

        verify(eventPublisher, never()).publishEvent(any(UserPresenceChangedEvent::class.java))
    }

    @Test
    fun `removeSession for last active device should emit OFFLINE presence event with lastSeen`() {
        val userId = "user-1"
        val sessionLaptop = "session-laptop"

        // Disconnecting laptop leaves 0 active sessions
        `when`(presenceCache.removeSession(userId, sessionLaptop))
            .thenReturn(0L)

        presenceService.removeSession(userId, sessionLaptop)

        val eventCaptor = ArgumentCaptor.forClass(UserPresenceChangedEvent::class.java)
        verify(eventPublisher).publishEvent(eventCaptor.capture())

        val event = eventCaptor.value
        assertThat(event.userId).isEqualTo(userId)
        assertThat(event.status).isEqualTo(UserStatus.OFFLINE)
        assertThat(event.sessionCount).isEqualTo(0)
        assertThat(event.lastSeen).isNotNull()
    }

    @Test
    fun `heartbeatSession should refresh session TTL without publishing events`() {
        val userId = "user-1"
        val sessionId = "session-phone"

        `when`(presenceCache.heartbeatSession(userId, sessionId))
            .thenReturn(true)

        presenceService.heartbeatSession(userId, sessionId)

        verify(presenceCache).heartbeatSession(userId, sessionId)
        verify(eventPublisher, never()).publishEvent(any())
    }

    @Test
    fun `heartbeatSession on expired session should re-register cleanly`() {
        val userId = "user-1"
        val sessionId = "session-recovered"

        `when`(presenceCache.heartbeatSession(userId, sessionId))
            .thenReturn(false)
        `when`(presenceCache.registerSession(userId, sessionId, null, "ANDROID", 45L))
            .thenReturn(0L)

        presenceService.heartbeatSession(userId, sessionId)

        verify(presenceCache).registerSession(userId, sessionId, null, "ANDROID", 45L)
    }

    @Test
    fun `reaper should detect expired sessions and transition user to OFFLINE`() {
        val userId = "user-1"
        `when`(presenceCache.getAllTrackedUserIds()).thenReturn(setOf(userId))
        `when`(presenceCache.getActiveSessionCount(userId)).thenReturn(0)

        // Seed online state
        `when`(presenceCache.registerSession(userId, "session-1", null, "ANDROID", 45L)).thenReturn(0L)
        presenceService.registerSession(userId, "session-1")

        // Run scheduled reaper
        presenceService.reapStaleSessions()

        val eventCaptor = ArgumentCaptor.forClass(UserPresenceChangedEvent::class.java)
        verify(eventPublisher, atLeastOnce()).publishEvent(eventCaptor.capture())

        val events = eventCaptor.allValues
        assertThat(events).hasSize(2) // 1st was ONLINE, 2nd is OFFLINE
        assertThat(events[1].status).isEqualTo(UserStatus.OFFLINE)
        assertThat(events[1].sessionCount).isEqualTo(0)
    }
}

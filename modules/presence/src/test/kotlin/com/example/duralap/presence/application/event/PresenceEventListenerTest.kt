package com.example.duralap.presence.application.event

import com.example.duralap.database.model.UserStatus
import com.example.duralap.events.UserPresenceChangedEvent
import com.example.duralap.user.api.UserModuleApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class PresenceEventListenerTest {

    @Mock
    private lateinit var simpMessagingTemplate: SimpMessagingTemplate

    @Mock
    private lateinit var userModuleApi: UserModuleApi

    private lateinit var eventListener: PresenceEventListener

    @BeforeEach
    fun setUp() {
        eventListener = PresenceEventListener(simpMessagingTemplate, userModuleApi)
    }

    @Test
    fun `handleUserPresenceChanged should broadcast to STOMP and update MongoDB on ONLINE`() {
        val event = UserPresenceChangedEvent(
            userId = "user-123",
            status = UserStatus.ONLINE,
            lastSeen = null,
            sessionCount = 1,
            timestamp = Instant.now()
        )

        eventListener.handleUserPresenceChanged(event)

        verify(simpMessagingTemplate).convertAndSend("/topic/presence/user-123", event)
        verify(simpMessagingTemplate).convertAndSend("/topic/presence", event)
        verify(userModuleApi).updateUserStatus("user-123", UserStatus.ONLINE)
    }

    @Test
    fun `handleUserPresenceChanged should broadcast to STOMP and update MongoDB on OFFLINE`() {
        val now = Instant.now()
        val event = UserPresenceChangedEvent(
            userId = "user-123",
            status = UserStatus.OFFLINE,
            lastSeen = now,
            sessionCount = 0,
            timestamp = now
        )

        eventListener.handleUserPresenceChanged(event)

        verify(simpMessagingTemplate).convertAndSend("/topic/presence/user-123", event)
        verify(simpMessagingTemplate).convertAndSend("/topic/presence", event)
        verify(userModuleApi).updateUserStatus("user-123", UserStatus.OFFLINE)
    }
}

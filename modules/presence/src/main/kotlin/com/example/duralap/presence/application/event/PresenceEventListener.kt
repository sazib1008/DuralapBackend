package com.example.duralap.presence.application.event

import com.example.duralap.database.model.UserStatus
import com.example.duralap.events.UserPresenceChangedEvent
import com.example.duralap.user.api.UserModuleApi
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class PresenceEventListener(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val userModuleApi: UserModuleApi
) {
    private val logger = LoggerFactory.getLogger(PresenceEventListener::class.java)

    @Async
    @EventListener
    fun handleUserPresenceChanged(event: UserPresenceChangedEvent) {
        try {
            logger.info("Broadcasting presence change for user={} status={}", event.userId, event.status)

            // 1. Deliver real-time presence to STOMP destination for this specific user
            simpMessagingTemplate.convertAndSend("/topic/presence/${event.userId}", event)

            // 2. Deliver real-time presence to global presence topic
            simpMessagingTemplate.convertAndSend("/topic/presence", event)

            // 3. Update MongoDB user record asynchronously on state transition
            if (event.status == UserStatus.OFFLINE) {
                userModuleApi.updateUserStatus(event.userId, UserStatus.OFFLINE)
            } else if (event.status == UserStatus.ONLINE) {
                userModuleApi.updateUserStatus(event.userId, UserStatus.ONLINE)
            }
        } catch (e: Exception) {
            logger.error("Error processing UserPresenceChangedEvent for user=${event.userId}", e)
        }
    }
}

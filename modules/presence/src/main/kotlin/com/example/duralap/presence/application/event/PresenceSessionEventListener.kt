package com.example.duralap.presence.application.event

import com.example.duralap.presence.application.service.PresenceService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class PresenceSessionEventListener(
    private val presenceService: PresenceService
) {
    private val logger = LoggerFactory.getLogger(PresenceSessionEventListener::class.java)

    @EventListener
    fun handleSessionConnected(event: SessionConnectedEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val userId = accessor.user?.name ?: accessor.sessionAttributes?.get("userId") as? String
        val sessionId = accessor.sessionId
        val deviceId = accessor.sessionAttributes?.get("deviceId") as? String
        val clientType = accessor.sessionAttributes?.get("clientType") as? String ?: "ANDROID"

        if (!userId.isNullOrBlank() && !sessionId.isNullOrBlank()) {
            logger.info("STOMP SessionConnectedEvent for user={}, session={}", userId, sessionId)
            presenceService.registerSession(userId, sessionId, deviceId, clientType)
        }
    }

    @EventListener
    fun handleSessionDisconnect(event: SessionDisconnectEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val userId = accessor.user?.name ?: accessor.sessionAttributes?.get("userId") as? String
        val sessionId = accessor.sessionId

        if (!userId.isNullOrBlank() && !sessionId.isNullOrBlank()) {
            logger.info("STOMP SessionDisconnectEvent for user={}, session={}", userId, sessionId)
            presenceService.removeSession(userId, sessionId)
        }
    }
}

package com.example.duralap.websocket

import com.example.duralap.database.model.UserStatus
import com.example.duralap.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener(
    private val userService: UserService,
    private val messagingTemplate: SimpMessageSendingOperations
) {

    private val logger = LoggerFactory.getLogger(WebSocketEventListener::class.java)

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        logger.info("Received a WebSocket connect event")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val sessionId = event.sessionId
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val username: String? = getUserFromHeader(headerAccessor)

        if (username != null) {
            logger.info("User Disconnected: $username")
            
            // Update user status to offline
            val userResponse = userService.getUserByUsername(username)
            if (userResponse != null) {
                userService.updateUserStatus(userResponse.id, UserStatus.OFFLINE)
                
                // Broadcast user left event
                messagingTemplate.convertAndSend("/topic/public", mapOf(
                    "username" to username,
                    "event" to "leave",
                    "timestamp" to System.currentTimeMillis()
                ))
            }
        }
    }

    private fun getUserFromHeader(headerAccessor: StompHeaderAccessor): String? {
        val userHeader = headerAccessor.getSessionAttributes()?.get("user")
        return if (userHeader is String) userHeader else null
    }
}
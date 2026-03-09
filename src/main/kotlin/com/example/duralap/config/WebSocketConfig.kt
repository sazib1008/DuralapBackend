package com.example.duralap.config

import com.example.duralap.security.JwtTokenProvider
import com.example.duralap.service.cache.UserPresenceCache
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userPresenceCache: UserPresenceCache
) : WebSocketMessageBrokerConfigurer {

    private val logger = LoggerFactory.getLogger(WebSocketConfig::class.java)

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket")
            .setAllowedOriginPatterns("*") // In production, specify exact origins
            // .withSockJS() Removed SockJS to enforce pure WebSocket (WSS) and decrease latency
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        // Fallback in-memory broker, ideally this points to RabbitMQ / Redis cluster via enableStompBrokerRelay
        registry.enableSimpleBroker("/topic", "/queue", "/user")
        registry.setApplicationDestinationPrefixes("/app")
        registry.setUserDestinationPrefix("/user")
    }

    override fun configureWebSocketTransport(registration: WebSocketTransportRegistration) {
        // Optional payload size optimizations for heavy WebRTC SDPs
        registration.setMessageSizeLimit(128 * 1024)
        registration.setSendTimeLimit(20000)
        registration.setSendBufferSizeLimit(512 * 1024)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

                if (accessor != null && StompCommand.CONNECT == accessor.command) {
                    val authHeaders = accessor.getNativeHeader("Authorization")
                    
                    if (!authHeaders.isNullOrEmpty()) {
                        val token = authHeaders[0].substring(7)
                        
                        if (jwtTokenProvider.validateToken(token)) {
                            val username = jwtTokenProvider.getUsernameFromToken(token)
                            val roles = jwtTokenProvider.getRolesFromToken(token).map { SimpleGrantedAuthority("ROLE_\$it") }
                            
                            val auth = UsernamePasswordAuthenticationToken(username, null, roles)
                            accessor.user = auth
                            
                            // User connected -> update presence to online
                            userPresenceCache.setUserOnline(username)
                            logger.info("WebSocket connected securely for user: \$username")
                        } else {
                            throw IllegalArgumentException("Invalid JWT token at WebSocket Connect")
                        }
                    } else {
                        throw IllegalArgumentException("Missing JWT token at WebSocket Connect")
                    }
                } else if (accessor != null && StompCommand.DISCONNECT == accessor.command) {
                    accessor.user?.name?.let { username ->
                        // User disconnected -> remove presence
                        userPresenceCache.setUserOffline(username)
                        logger.info("WebSocket disconnected for user: \$username")
                    }
                }

                return message
            }
        })
    }
}
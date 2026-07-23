package com.example.duralap.config

import com.example.duralap.security.JwtTokenProvider
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

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val jwtTokenProvider: JwtTokenProvider
) : WebSocketMessageBrokerConfigurer {

    private val logger = LoggerFactory.getLogger(WebSocketConfig::class.java)

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket")
            .setAllowedOriginPatterns("*")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic", "/queue", "/user")
        registry.setApplicationDestinationPrefixes("/app")
        registry.setUserDestinationPrefix("/user")
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
                            val roles = jwtTokenProvider.getRolesFromToken(token).map { SimpleGrantedAuthority("ROLE_$it") }
                            
                            val auth = UsernamePasswordAuthenticationToken(username, null, roles)
                            accessor.user = auth
                            logger.info("WebSocket notifications connected securely for user: $username")
                        } else {
                            throw IllegalArgumentException("Invalid JWT token at WebSocket Connect")
                        }
                    } else {
                        throw IllegalArgumentException("Missing JWT token at WebSocket Connect")
                    }
                }

                return message
            }
        })
    }
}

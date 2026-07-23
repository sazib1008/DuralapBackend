package com.example.duralap.message

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@SpringBootApplication(scanBasePackages = ["com.example.duralap"])
@EnableWebSocketMessageBroker
class MessageServiceApplication : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("*")
    }
}

fun main(args: Array<String>) {
    runApplication<MessageServiceApplication>(*args)
}

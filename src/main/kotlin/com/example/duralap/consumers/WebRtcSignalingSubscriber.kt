package com.example.duralap.consumers

import com.example.duralap.service.signaling.WebRtcSignalPayload
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class WebRtcSignalingSubscriber(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) : MessageListener {

    private val logger = LoggerFactory.getLogger(WebRtcSignalingSubscriber::class.java)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val channel = String(message.channel)
            val body = String(message.body)

            // Extract targetUserId from the channel. e.g., "rtc:signal:user:12345"
            val targetUserId = channel.substringAfterLast(":")

            val payload = objectMapper.readValue(body, WebRtcSignalPayload::class.java)

            // Bypassing Principal lookup by using a direct UUID-specific topic:
            // Clients must subscribe to: /topic/user/{userId}/signaling
            val destination = "/topic/user/$targetUserId/signaling"
            
            simpMessagingTemplate.convertAndSend(destination, payload)

            logger.debug("Successfully relayed WebRTC signal to user: {} at {}", targetUserId, destination)
        } catch (e: Exception) {
            logger.error("Failed to process WebRTC signal from Redis", e)
        }
    }
}

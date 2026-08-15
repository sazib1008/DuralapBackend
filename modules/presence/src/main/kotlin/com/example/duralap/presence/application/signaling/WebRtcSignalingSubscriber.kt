package com.example.duralap.presence.application.signaling

import com.example.duralap.database.dto.CallSignalingMessage
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

            val targetUserId = channel.substringAfterLast(":")
            val payload = objectMapper.readValue(body, CallSignalingMessage::class.java)

            // Deliver to user topic destination (subscribed by mobile client)
            val destination = "/topic/user/$targetUserId/signaling"
            simpMessagingTemplate.convertAndSend(destination, payload)

            // Also send to user personal queue for dual destination compatibility
            val userQueueDestination = "/user/$targetUserId/queue/calls"
            simpMessagingTemplate.convertAndSend(userQueueDestination, payload)

            logger.debug("Successfully relayed WebRTC signal to user: {} (type={}, callId={})", targetUserId, payload.signalType, payload.callId)
        } catch (e: Exception) {
            logger.error("Failed to process WebRTC signal from Redis", e)
        }
    }
}

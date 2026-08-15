package com.example.duralap.presence.application.signaling

import com.example.duralap.database.dto.CallSignalingMessage
import com.example.duralap.database.dto.SignalType
import com.example.duralap.database.model.CallType
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class CallSignalingService(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(CallSignalingService::class.java)

    companion object {
        const val SIGNALING_CHANNEL_PREFIX = "rtc:signal:user:"
    }

    /**
     * Dispatches a signaling message across Redis Pub/Sub so whichever backend instance
     * holds the target user's WebSocket connection can forward it over STOMP.
     */
    fun dispatchSignalToUser(targetUserId: String, message: CallSignalingMessage) {
        try {
            val channel = "$SIGNALING_CHANNEL_PREFIX$targetUserId"
            val json = objectMapper.writeValueAsString(message)
            redisTemplate.convertAndSend(channel, json)
            logger.debug("Dispatched signaling message to user {}: type={}, callId={}", targetUserId, message.signalType, message.callId)
        } catch (e: Exception) {
            logger.error("Failed to dispatch signaling message to user {}", targetUserId, e)
        }
    }

    /**
     * Convenience helper to send a simple lifecycle signal (e.g. RINGING, REJECT, BUSY, CANCEL, END, TIMEOUT).
     */
    fun sendLifecycleSignal(
        callId: String,
        senderId: String,
        targetUserId: String,
        signalType: SignalType,
        callType: CallType = CallType.AUDIO,
        conversationId: String? = null,
        reason: String? = null
    ) {
        val signal = CallSignalingMessage(
            callId = callId,
            conversationId = conversationId,
            callerId = if (signalType == SignalType.CALL_INITIATE) senderId else null,
            calleeId = if (signalType == SignalType.CALL_INITIATE) targetUserId else null,
            senderId = senderId,
            targetUserId = targetUserId,
            callType = callType,
            signalType = signalType,
            reason = reason
        )
        dispatchSignalToUser(targetUserId, signal)
    }
}

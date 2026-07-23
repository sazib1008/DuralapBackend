package com.example.duralap.service.signaling

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class CallSignalingService(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    fun dispatchSignalToUser(targetUserId: String, payload: WebRtcSignalPayload) {
        val routingKey = "rtc:signal:user:$targetUserId"
        val json = objectMapper.writeValueAsString(payload)
        redisTemplate.convertAndSend(routingKey, json)
    }
}

data class WebRtcSignalPayload(
    val callId: String,
    val senderId: String,
    val type: SignalType, // OFFER, ANSWER, ICE_CANDIDATE
    val sdpData: String? = null,
    val iceCandidate: String? = null
)

enum class SignalType {
    OFFER, ANSWER, ICE_CANDIDATE
}

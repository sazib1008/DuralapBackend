package com.example.duralap.presence.web.controller

import com.example.duralap.database.dto.CallInitiateRequest
import com.example.duralap.database.dto.CallSignalingMessage
import com.example.duralap.database.dto.SignalType
import com.example.duralap.presence.application.service.CallService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

@Controller
class CallSignalingController(
    private val callService: CallService
) {
    private val logger = LoggerFactory.getLogger(CallSignalingController::class.java)

    /**
     * STOMP handler for low-latency call initiation: /app/call.initiate
     */
    @MessageMapping("/call.initiate")
    fun handleCallInitiate(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: CallInitiateRequest
    ) {
        val authUserId = headerAccessor.user?.name ?: headerAccessor.sessionAttributes?.get("userId") as? String
        if (authUserId.isNullOrBlank()) {
            logger.warn("Unauthenticated attempt to initiate call via STOMP")
            return
        }

        // Enforce caller is authenticated user
        val safeRequest = request.copy(callerId = authUserId)
        try {
            callService.initiateCall(safeRequest)
        } catch (e: Exception) {
            logger.error("Failed to initiate call via STOMP for user: {}", authUserId, e)
        }
    }

    /**
     * STOMP handler for all real-time call signaling: /app/call.signal
     * Relays SDP Offer, SDP Answer, ICE Candidates, and Lifecycle updates.
     */
    @MessageMapping("/call.signal")
    fun handleCallSignal(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload signal: CallSignalingMessage
    ) {
        val authUserId = headerAccessor.user?.name ?: headerAccessor.sessionAttributes?.get("userId") as? String
        if (authUserId.isNullOrBlank()) {
            logger.warn("Unauthenticated attempt to send call signal via STOMP")
            return
        }

        try {
            when (signal.signalType) {
                SignalType.CALL_ACCEPT -> {
                    callService.acceptCall(signal.callId, authUserId)
                }
                SignalType.CALL_REJECT -> {
                    callService.rejectCall(signal.callId, authUserId, signal.reason ?: "DECLINED")
                }
                SignalType.CALL_CANCEL -> {
                    callService.cancelCall(signal.callId, authUserId)
                }
                SignalType.CALL_END -> {
                    callService.endCall(signal.callId, authUserId)
                }
                else -> {
                    // Forward WebRTC SDP / ICE / Ringing signals directly
                    val safeSignal = signal.copy(senderId = authUserId)
                    callService.processWebRtcSignal(safeSignal, authUserId)
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing call signal {} for callId {}", signal.signalType, signal.callId, e)
        }
    }
}


package com.example.duralap.websocket

import com.example.duralap.database.dto.CallInitiateRequest
import com.example.duralap.service.CallService
import com.example.duralap.service.ConversationService
import com.example.duralap.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class CallController(
    private val callService: CallService,
    private val conversationService: ConversationService,
    private val userService: UserService,
    private val messagingTemplate: SimpMessageSendingOperations
) {

    private val logger = LoggerFactory.getLogger(CallController::class.java)

    /**
     * Handle incoming call initiation
     */
    @MessageMapping("/call.initiate")
    fun initiateCall(@Payload callRequest: CallInitiateRequest) {
        try {
            logger.info("Initiating call from ${callRequest.callerId} to ${callRequest.calleeId}")
            
            val callResponse = callService.initiateCall(callRequest)
            
            // Notify callee about incoming call
            val notification = mapOf(
                "type" to "INCOMING_CALL",
                "callId" to callResponse.id,
                "callerId" to callResponse.callerId,
                "calleeId" to callResponse.calleeId,
                "callType" to callResponse.callType.name,
                "conversationId" to callResponse.conversationId,
                "timestamp" to System.currentTimeMillis()
            )
            
            messagingTemplate.convertAndSend("/user/${callResponse.calleeId}/queue/calls", notification)
            
            // Notify caller that call is initiated
            val callerNotification = mapOf(
                "type" to "CALL_INITIATED",
                "callId" to callResponse.id,
                "status" to "RINGING"
            )
            messagingTemplate.convertAndSend("/user/${callResponse.callerId}/queue/calls", callerNotification)
            
        } catch (e: Exception) {
            logger.error("Error initiating call", e)
            // Send error to caller
            messagingTemplate.convertAndSend("/user/${callRequest.callerId}/queue/calls", 
                mapOf("type" to "CALL_ERROR", "message" to e.message))
        }
    }

    /**
     * Handle call acceptance
     */
    @MessageMapping("/call.accept")
    fun acceptCall(@Payload callAction: CallActionRequest) {
        try {
            logger.info("Accepting call ${callAction.callId} by user ${callAction.userId}")
            
            val callResponse = callService.acceptCall(callAction.callId, callAction.userId)
            
            // Notify both parties about call acceptance
            val notification = mapOf(
                "type" to "CALL_ACCEPTED",
                "callId" to callResponse.id,
                "acceptedBy" to callAction.userId,
                "status" to "ACTIVE",
                "timestamp" to System.currentTimeMillis()
            )
            
            messagingTemplate.convertAndSend("/user/${callResponse.callerId}/queue/calls", notification)
            messagingTemplate.convertAndSend("/user/${callResponse.calleeId}/queue/calls", notification)
            
        } catch (e: Exception) {
            logger.error("Error accepting call", e)
            messagingTemplate.convertAndSend("/user/${callAction.userId}/queue/calls", 
                mapOf("type" to "CALL_ERROR", "message" to e.message))
        }
    }

    /**
     * Handle call rejection
     */
    @MessageMapping("/call.reject")
    fun rejectCall(@Payload callAction: CallActionRequest) {
        try {
            logger.info("Rejecting call ${callAction.callId} by user ${callAction.userId}")
            
            val callResponse = callService.rejectCall(callAction.callId, callAction.userId)
            
            // Notify caller about rejection
            val notification = mapOf(
                "type" to "CALL_REJECTED",
                "callId" to callResponse.id,
                "rejectedBy" to callAction.userId,
                "status" to "REJECTED",
                "timestamp" to System.currentTimeMillis()
            )
            
            messagingTemplate.convertAndSend("/user/${callResponse.callerId}/queue/calls", notification)
            
        } catch (e: Exception) {
            logger.error("Error rejecting call", e)
            messagingTemplate.convertAndSend("/user/${callAction.userId}/queue/calls", 
                mapOf("type" to "CALL_ERROR", "message" to e.message))
        }
    }

    /**
     * Handle call ending
     */
    @MessageMapping("/call.end")
    fun endCall(@Payload callAction: CallActionRequest) {
        try {
            logger.info("Ending call ${callAction.callId} by user ${callAction.userId}")
            
            val callResponse = callService.endCall(callAction.callId, callAction.userId)
            
            // Notify both parties about call end
            val notification = mapOf(
                "type" to "CALL_ENDED",
                "callId" to callResponse.id,
                "endedBy" to callAction.userId,
                "status" to "ENDED",
                "duration" to callResponse.duration,
                "timestamp" to System.currentTimeMillis()
            )
            
            messagingTemplate.convertAndSend("/user/${callResponse.callerId}/queue/calls", notification)
            messagingTemplate.convertAndSend("/user/${callResponse.calleeId}/queue/calls", notification)
            
        } catch (e: Exception) {
            logger.error("Error ending call", e)
            messagingTemplate.convertAndSend("/user/${callAction.userId}/queue/calls", 
                mapOf("type" to "CALL_ERROR", "message" to e.message))
        }
    }

    /**
     * Handle WebRTC signaling (offer/answer/ice-candidates)
     */
    @MessageMapping("/call.signal")
    fun handleWebRTCSignaling(@Payload signal: WebRTCSignal) {
        try {
            logger.info("Handling WebRTC signal from ${signal.senderId} to ${signal.targetId}")
            
            val notification = mapOf(
                "type" to "WEBRTC_SIGNAL",
                "callId" to signal.callId,
                "senderId" to signal.senderId,
                "targetId" to signal.targetId,
                "signalType" to signal.type.name,
                "data" to signal.data,
                "timestamp" to System.currentTimeMillis()
            )
            
            messagingTemplate.convertAndSend("/user/${signal.targetId}/queue/calls", notification)
            
        } catch (e: Exception) {
            logger.error("Error handling WebRTC signaling", e)
        }
    }
}

data class CallActionRequest(
    val callId: String,
    val userId: String
)

data class WebRTCSignal(
    val callId: String,
    val senderId: String,
    val targetId: String,
    val type: String, // "offer", "answer", "ice-candidate"
    val data: String // SDP or ICE candidate data
)
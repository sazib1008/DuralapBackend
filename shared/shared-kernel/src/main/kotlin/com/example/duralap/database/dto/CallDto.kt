package com.example.duralap.database.dto

import com.example.duralap.database.model.CallType
import com.example.duralap.database.model.CallStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CallInitiateRequest(
    @field:NotBlank(message = "Conversation ID is required")
    val conversationId: String,
    
    @field:NotBlank(message = "Caller ID is required")
    val callerId: String,
    
    @field:NotBlank(message = "Callee ID is required")
    val calleeId: String,
    
    @field:NotNull(message = "Call type is required")
    val callType: CallType
)

data class CallResponse(
    val id: String,
    val conversationId: String,
    val callerId: String,
    val calleeId: String,
    val callType: CallType,
    val status: CallStatus,
    val startTime: java.time.Instant?,
    val endTime: java.time.Instant?,
    val duration: Long?,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant
)

data class CallActionRequest(
    @field:NotBlank(message = "Call ID is required")
    val callId: String,
    
    @field:NotBlank(message = "User ID is required")
    val userId: String,
    
    @field:NotNull(message = "Call status is required")
    val status: CallStatus
)

data class WebRTCSignal(
    @field:NotBlank(message = "Call ID is required")
    val callId: String,
    
    @field:NotBlank(message = "Sender ID is required")
    val senderId: String,
    
    @field:NotBlank(message = "Target ID is required")
    val targetId: String,
    
    @field:NotBlank(message = "Signal type is required")
    val type: SignalType, // "offer", "answer", "ice-candidate"
    
    @field:NotBlank(message = "Signal data is required")
    val data: String // SDP or ICE candidate data
)

enum class SignalType {
    CALL_INITIATE,
    CALL_RINGING,
    CALL_ACCEPT,
    CALL_REJECT,
    CALL_BUSY,
    CALL_CANCEL,
    CALL_END,
    CALL_TIMEOUT,
    WEBRTC_OFFER,
    WEBRTC_ANSWER,
    ICE_CANDIDATE,
    CALL_CONNECTED,
    CALL_FAILED,
    // Backward compatibility aliases
    OFFER,
    ANSWER
}

data class CallSignalingMessage(
    val callId: String,
    val conversationId: String? = null,
    val callerId: String? = null,
    val calleeId: String? = null,
    val senderId: String,
    val targetUserId: String,
    val callType: CallType = CallType.AUDIO,
    val signalType: SignalType,
    val callerName: String? = null,
    val callerAvatar: String? = null,
    val sdp: String? = null,
    val sdpData: String? = null,
    val iceCandidate: String? = null,
    val sdpMid: String? = null,
    val sdpMLineIndex: Int? = null,
    val reason: String? = null,
    val timestamp: java.time.Instant = java.time.Instant.now()
)

data class IceServerConfig(
    val urls: List<String>,
    val username: String? = null,
    val credential: String? = null
)

data class CallIceServersResponse(
    val iceServers: List<IceServerConfig>
)

data class CallStatusUpdate(
    val callId: String,
    val status: CallStatus,
    val userId: String,
    val timestamp: java.time.Instant = java.time.Instant.now()
)

data class CallHistoryItemResponse(
    val id: String,
    val conversationId: String,
    val callerId: String,
    val calleeId: String,
    val callType: CallType,
    val status: CallStatus,
    val startTime: java.time.Instant?,
    val endTime: java.time.Instant?,
    val duration: Long?,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant,
    val otherUser: PublicUserProfile?,
    val isIncoming: Boolean
)
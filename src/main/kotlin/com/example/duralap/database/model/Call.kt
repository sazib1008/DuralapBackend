package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("calls")
data class Call(
    @Id val id: String? = null,
    val conversationId: String,
    val callerId: String,
    val calleeId: String,
    val callType: CallType,
    val status: CallStatus = CallStatus.INITIATED,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val duration: Long? = null, // in seconds
    val iceCandidates: List<String> = emptyList(), // WebRTC ICE candidates
    val offer: String? = null, // WebRTC offer SDP
    val answer: String? = null, // WebRTC answer SDP
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallStatus {
    INITIATED,    // Call initiated but not answered
    RINGING,      // Call is ringing
    ACTIVE,       // Call is active
    ENDED,        // Call ended normally
    REJECTED,     // Call rejected by callee
    MISSED,       // Call missed by callee
    FAILED,       // Call failed due to technical issues
    BUSY          // Callee is busy in another call
}
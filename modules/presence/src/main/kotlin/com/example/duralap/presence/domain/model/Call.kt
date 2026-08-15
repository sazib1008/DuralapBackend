package com.example.duralap.presence.domain.model

import com.example.duralap.database.dto.CallResponse
import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("calls")
@CompoundIndexes(
    CompoundIndex(name = "callee_status_idx", def = "{'calleeId': 1, 'status': 1}"),
    CompoundIndex(name = "caller_status_idx", def = "{'callerId': 1, 'status': 1}"),
    CompoundIndex(name = "users_created_idx", def = "{'callerId': 1, 'calleeId': 1, 'createdAt': -1}"),
    CompoundIndex(name = "conv_created_idx", def = "{'conversationId': 1, 'createdAt': -1}")
)
data class Call(
    @Id
    val id: String? = null,

    @Indexed
    val conversationId: String,

    @Indexed
    val callerId: String,

    @Indexed
    val calleeId: String,

    val callType: CallType = CallType.AUDIO,

    @Indexed
    val status: CallStatus = CallStatus.INITIATED,

    val startTime: Instant? = null,

    val endTime: Instant? = null,

    val duration: Long? = null,

    val endReason: String? = null,

    val createdAt: Instant = Instant.now(),

    val updatedAt: Instant = Instant.now()
)

fun Call.toCallResponse(): CallResponse {
    return CallResponse(
        id = this.id ?: throw IllegalStateException("Call ID cannot be null"),
        conversationId = this.conversationId,
        callerId = this.callerId,
        calleeId = this.calleeId,
        callType = this.callType,
        status = this.status,
        startTime = this.startTime,
        endTime = this.endTime,
        duration = this.duration,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

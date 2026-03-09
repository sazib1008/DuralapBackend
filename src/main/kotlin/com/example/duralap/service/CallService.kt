package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Call
import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import com.example.duralap.database.repository.CallRepository
import com.example.duralap.service.cache.UserPresenceCache
import com.example.duralap.service.signaling.CallSignalingService
import com.example.duralap.service.signaling.WebRtcSignalPayload
import com.example.duralap.service.signaling.SignalType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class CallService(
    private val callRepository: CallRepository,
    private val presenceCache: UserPresenceCache,
    private val callSignalingService: CallSignalingService
) {

    @Transactional
    fun initiateCall(request: CallInitiateRequest): CallResponse {
        // Optional presence validation could go here. For now, we initiate regardless.
        // if (!presenceCache.isUserOnline(request.calleeId)) { ... }

        if (callRepository.countCallsByStatusForUser(request.calleeId, CallStatus.ACTIVE) > 0) {
            val busyCall = Call(
                id = UUID.randomUUID().toString(),
                conversationId = request.conversationId,
                callerId = request.callerId,
                calleeId = request.calleeId,
                callType = request.callType,
                status = CallStatus.BUSY,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            return callRepository.save(busyCall).toCallResponse()
        }

        val call = Call(
            id = UUID.randomUUID().toString(),
            conversationId = request.conversationId,
            callerId = request.callerId,
            calleeId = request.calleeId,
            callType = request.callType,
            status = CallStatus.RINGING,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        return callRepository.save(call).toCallResponse()
    }

    fun acceptCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        if (call.calleeId != userId) {
            throw IllegalArgumentException("You are not the callee for this call")
        }

        if (call.status != CallStatus.RINGING) {
            throw IllegalArgumentException("Call is no longer ringing")
        }

        val updatedCall = call.copy(
            status = CallStatus.ACTIVE,
            startTime = Instant.now(),
            updatedAt = Instant.now()
        )

        return callRepository.save(updatedCall).toCallResponse()
    }

    fun rejectCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        if (call.calleeId != userId) {
            throw IllegalArgumentException("You are not the callee for this call")
        }

        val updatedCall = call.copy(
            status = CallStatus.REJECTED,
            endTime = Instant.now(),
            updatedAt = Instant.now()
        )

        return callRepository.save(updatedCall).toCallResponse()
    }

    fun endCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        if (call.callerId != userId && call.calleeId != userId) {
            throw IllegalArgumentException("You are not a participant in this call")
        }

        val duration = if (call.startTime != null) {
            java.time.Duration.between(call.startTime, Instant.now()).seconds
        } else null

        val updatedCall = call.copy(
            status = CallStatus.ENDED,
            endTime = Instant.now(),
            duration = duration,
            updatedAt = Instant.now()
        )

        return callRepository.save(updatedCall).toCallResponse()
    }

    fun getCallById(callId: String): CallResponse? {
        return callRepository.findByIdOrNull(callId)?.toCallResponse()
    }

    fun getActiveCallsForUser(userId: String): List<CallResponse> {
        return callRepository.findActiveCallsForUser(userId).map { it.toCallResponse() }
    }

    fun getRecentCallsForUser(userId: String, limit: Int = 20): List<CallResponse> {
        return callRepository.findRecentCallsForUser(userId)
            .sortedByDescending { it.createdAt }
            .take(limit)
            .map { it.toCallResponse() }
    }

    fun getMissedCallsForUser(userId: String): List<CallResponse> {
        return callRepository.findMissedCallsForUser(userId).map { it.toCallResponse() }
    }

    fun getCallHistory(user1Id: String, user2Id: String, limit: Int = 50): List<CallResponse> {
        return callRepository.findCallsBetweenUsers(user1Id, user2Id)
            .sortedByDescending { it.createdAt }
            .take(limit)
            .map { it.toCallResponse() }
    }

    fun getOngoingCalls(): List<CallResponse> {
        return callRepository.findOngoingCalls().map { it.toCallResponse() }
    }

    /**
     * Defers WebRTC Signaling entirely effectively to Redis Pub/Sub, NO DB writes.
     */
    fun updateCallWithSignaling(callId: String, offer: String? = null, answer: String? = null, iceCandidates: List<String>? = null): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")
            
        // Send to both since HTTP controller endpoint doesn't supply sender explicitly
        val participants = listOf(call.callerId, call.calleeId)

        participants.forEach { targetUserId ->
            if (offer != null) {
                callSignalingService.dispatchSignalToUser(targetUserId, WebRtcSignalPayload(callId, "sync", SignalType.OFFER, sdpData = offer))
            }
            if (answer != null) {
                callSignalingService.dispatchSignalToUser(targetUserId, WebRtcSignalPayload(callId, "sync", SignalType.ANSWER, sdpData = answer))
            }
            iceCandidates?.forEach { ice ->
                callSignalingService.dispatchSignalToUser(targetUserId, WebRtcSignalPayload(callId, "sync", SignalType.ICE_CANDIDATE, iceCandidate = ice))
            }
        }

        // Return call response without persisting the payload
        return call.toCallResponse()
    }

    /**
     * Analytics Refactor: Uses DB-level aggregations
     */
    fun getCallStats(userId: String): Map<String, Any> {
        return mapOf(
            "totalCalls" to callRepository.countCallsByStatusForUser(userId, CallStatus.ENDED),
            "missedCalls" to callRepository.countCallsByStatusForUser(userId, CallStatus.MISSED),
            "rejectedCalls" to callRepository.countCallsByStatusForUser(userId, CallStatus.REJECTED),
            "activeCalls" to callRepository.findActiveCallsForUser(userId).size,
            "audioCalls" to callRepository.countByCallTypeAndUser(CallType.AUDIO, userId),
            "videoCalls" to callRepository.countByCallTypeAndUser(CallType.VIDEO, userId)
        )
    }
}

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
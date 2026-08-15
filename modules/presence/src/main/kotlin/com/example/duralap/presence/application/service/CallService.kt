package com.example.duralap.presence.application.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.*
import com.example.duralap.presence.application.cache.CallAcquireStatus
import com.example.duralap.presence.application.cache.CallSessionRedisCache
import com.example.duralap.presence.application.signaling.CallSignalingService
import com.example.duralap.presence.domain.model.Call
import com.example.duralap.presence.domain.model.toCallResponse
import com.example.duralap.presence.domain.repository.CallRepository
import com.example.duralap.user.api.UserModuleApi
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class CallService(
    private val callRepository: CallRepository,
    private val callSessionRedisCache: CallSessionRedisCache,
    private val callSignalingService: CallSignalingService,
    private val userModuleApi: UserModuleApi,
    @Value("\${webrtc.turn.url:}") private val turnUrl: String = "",
    @Value("\${webrtc.turn.username:}") private val turnUsername: String = "",
    @Value("\${webrtc.turn.credential:}") private val turnCredential: String = ""
) {
    private val logger = LoggerFactory.getLogger(CallService::class.java)

    /**
     * Initiates a new 1-to-1 voice or video call.
     * Uses atomic Redis session acquisition to guarantee no collision.
     */
    @Transactional
    fun initiateCall(request: CallInitiateRequest): CallResponse {
        val callId = UUID.randomUUID().toString()
        val now = Instant.now()

        // 1. Atomic Redis check to see if caller or callee is currently in another call
        val acquireResult = callSessionRedisCache.tryAcquireCall(
            callId = callId,
            callerId = request.callerId,
            calleeId = request.calleeId,
            conversationId = request.conversationId,
            callType = request.callType
        )

        if (acquireResult.status == CallAcquireStatus.CALLEE_BUSY) {
            logger.info("Call {} rejected: Callee {} is already in call {}", callId, request.calleeId, acquireResult.existingCallId)
            val busyCall = Call(
                id = callId,
                conversationId = request.conversationId,
                callerId = request.callerId,
                calleeId = request.calleeId,
                callType = request.callType,
                status = CallStatus.BUSY,
                endReason = "CALLEE_BUSY",
                createdAt = now,
                updatedAt = now
            )
            val saved = callRepository.save(busyCall)
            callSignalingService.sendLifecycleSignal(
                callId = callId,
                senderId = request.calleeId,
                targetUserId = request.callerId,
                signalType = SignalType.CALL_BUSY,
                callType = request.callType,
                conversationId = request.conversationId,
                reason = "CALLEE_BUSY"
            )
            return saved.toCallResponse()
        }

        if (acquireResult.status == CallAcquireStatus.CALLER_BUSY) {
            logger.info("Call {} rejected: Caller {} is already in call {}", callId, request.callerId, acquireResult.existingCallId)
            val busyCall = Call(
                id = callId,
                conversationId = request.conversationId,
                callerId = request.callerId,
                calleeId = request.calleeId,
                callType = request.callType,
                status = CallStatus.BUSY,
                endReason = "CALLER_BUSY",
                createdAt = now,
                updatedAt = now
            )
            return callRepository.save(busyCall).toCallResponse()
        }

        // 2. Persist initial Call record in MongoDB
        val call = Call(
            id = callId,
            conversationId = request.conversationId,
            callerId = request.callerId,
            calleeId = request.calleeId,
            callType = request.callType,
            status = CallStatus.RINGING,
            createdAt = now,
            updatedAt = now
        )
        val savedCall = callRepository.save(call)

        // 3. Look up caller profile to include name and avatar in incoming call notification
        val callerProfile = userModuleApi.findUserById(request.callerId)?.toPublicProfile()
        val callerName = callerProfile?.fullName?.takeIf { it.isNotBlank() } ?: callerProfile?.username ?: "Contact"
        val callerAvatar = callerProfile?.profileImageUrl

        // 4. Send real-time CALL_INITIATE signaling frame to callee
        val initiateSignal = CallSignalingMessage(
            callId = callId,
            conversationId = request.conversationId,
            callerId = request.callerId,
            calleeId = request.calleeId,
            senderId = request.callerId,
            targetUserId = request.calleeId,
            callType = request.callType,
            signalType = SignalType.CALL_INITIATE,
            callerName = callerName,
            callerAvatar = callerAvatar,
            timestamp = now
        )
        callSignalingService.dispatchSignalToUser(request.calleeId, initiateSignal)

        // 5. Send CALL_RINGING ack back to caller
        val ringingSignal = CallSignalingMessage(
            callId = callId,
            conversationId = request.conversationId,
            callerId = request.callerId,
            calleeId = request.calleeId,
            senderId = request.calleeId,
            targetUserId = request.callerId,
            callType = request.callType,
            signalType = SignalType.CALL_RINGING,
            timestamp = now
        )
        callSignalingService.dispatchSignalToUser(request.callerId, ringingSignal)

        logger.info("Call {} initiated by {} to {} (type={})", callId, request.callerId, request.calleeId, request.callType)
        return savedCall.toCallResponse()
    }

    /**
     * Callee accepts the call.
     */
    @Transactional
    fun acceptCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found with id: $callId")

        if (call.calleeId != userId) {
            throw IllegalArgumentException("Only the callee can accept this call")
        }

        val now = Instant.now()
        val updatedCall = call.copy(
            status = CallStatus.CONNECTED,
            startTime = now,
            updatedAt = now
        )
        val saved = callRepository.save(updatedCall)

        // Extend Redis session to active call duration (1 hour)
        callSessionRedisCache.updateCallStatus(callId, CallStatus.CONNECTED, extendTtl = true)

        // Notify caller that call was accepted
        callSignalingService.sendLifecycleSignal(
            callId = callId,
            senderId = userId,
            targetUserId = call.callerId,
            signalType = SignalType.CALL_ACCEPT,
            callType = call.callType,
            conversationId = call.conversationId
        )

        logger.info("Call {} accepted by {}", callId, userId)
        return saved.toCallResponse()
    }

    /**
     * Callee rejects the call.
     */
    @Transactional
    fun rejectCall(callId: String, userId: String, reason: String = "DECLINED"): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found with id: $callId")

        if (call.calleeId != userId) {
            throw IllegalArgumentException("Only the callee can reject this call")
        }

        val now = Instant.now()
        val updatedCall = call.copy(
            status = CallStatus.REJECTED,
            endTime = now,
            endReason = reason,
            updatedAt = now
        )
        val saved = callRepository.save(updatedCall)

        // Release Redis lock
        callSessionRedisCache.releaseCall(callId, call.callerId, call.calleeId)

        // Notify caller that call was rejected
        callSignalingService.sendLifecycleSignal(
            callId = callId,
            senderId = userId,
            targetUserId = call.callerId,
            signalType = SignalType.CALL_REJECT,
            callType = call.callType,
            conversationId = call.conversationId,
            reason = reason
        )

        logger.info("Call {} rejected by {} (reason={})", callId, userId, reason)
        return saved.toCallResponse()
    }

    /**
     * Caller cancels the call before callee answers.
     */
    @Transactional
    fun cancelCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found with id: $callId")

        if (call.callerId != userId) {
            throw IllegalArgumentException("Only the caller can cancel this call")
        }

        val now = Instant.now()
        val updatedCall = call.copy(
            status = CallStatus.CANCELLED,
            endTime = now,
            endReason = "CANCELLED_BY_CALLER",
            updatedAt = now
        )
        val saved = callRepository.save(updatedCall)

        // Release Redis locks
        callSessionRedisCache.releaseCall(callId, call.callerId, call.calleeId)

        // Notify callee to dismiss incoming call UI
        callSignalingService.sendLifecycleSignal(
            callId = callId,
            senderId = userId,
            targetUserId = call.calleeId,
            signalType = SignalType.CALL_CANCEL,
            callType = call.callType,
            conversationId = call.conversationId,
            reason = "CANCELLED_BY_CALLER"
        )

        logger.info("Call {} cancelled by caller {}", callId, userId)
        return saved.toCallResponse()
    }

    /**
     * Either participant ends an active call.
     */
    @Transactional
    fun endCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found with id: $callId")

        if (call.callerId != userId && call.calleeId != userId) {
            throw IllegalArgumentException("You are not a participant in this call")
        }

        val now = Instant.now()
        val duration = if (call.startTime != null) {
            Duration.between(call.startTime, now).seconds
        } else null

        val updatedCall = call.copy(
            status = CallStatus.ENDED,
            endTime = now,
            duration = duration,
            endReason = "NORMAL_HANGUP",
            updatedAt = now
        )
        val saved = callRepository.save(updatedCall)

        // Release Redis locks
        callSessionRedisCache.releaseCall(callId, call.callerId, call.calleeId)

        // Notify other participant
        val otherUserId = if (call.callerId == userId) call.calleeId else call.callerId
        callSignalingService.sendLifecycleSignal(
            callId = callId,
            senderId = userId,
            targetUserId = otherUserId,
            signalType = SignalType.CALL_END,
            callType = call.callType,
            conversationId = call.conversationId,
            reason = "NORMAL_HANGUP"
        )

        logger.info("Call {} ended by {} (duration={}s)", callId, userId, duration)
        return saved.toCallResponse()
    }

    /**
     * Handles timeout when call is unanswered.
     */
    @Transactional
    fun timeoutCall(callId: String): CallResponse? {
        val call = callRepository.findByIdOrNull(callId) ?: return null
        if (call.status != CallStatus.RINGING && call.status != CallStatus.INITIATED) {
            return call.toCallResponse()
        }

        val now = Instant.now()
        val updatedCall = call.copy(
            status = CallStatus.MISSED,
            endTime = now,
            endReason = "TIMEOUT",
            updatedAt = now
        )
        val saved = callRepository.save(updatedCall)

        callSessionRedisCache.releaseCall(callId, call.callerId, call.calleeId)

        // Notify both parties of timeout
        callSignalingService.sendLifecycleSignal(
            callId = callId,
            senderId = "system",
            targetUserId = call.callerId,
            signalType = SignalType.CALL_TIMEOUT,
            callType = call.callType,
            conversationId = call.conversationId,
            reason = "TIMEOUT"
        )
        callSignalingService.sendLifecycleSignal(
            callId = callId,
            senderId = "system",
            targetUserId = call.calleeId,
            signalType = SignalType.CALL_TIMEOUT,
            callType = call.callType,
            conversationId = call.conversationId,
            reason = "TIMEOUT"
        )

        logger.info("Call {} timed out (marked MISSED)", callId)
        return saved.toCallResponse()
    }

    /**
     * Relays real-time WebRTC signaling (Offer, Answer, ICE candidates).
     */
    fun processWebRtcSignal(signal: CallSignalingMessage, authenticatedUserId: String) {
        if (signal.senderId != authenticatedUserId) {
            logger.warn("[SIGNALING_REJECTED] senderId {} does not match authenticated user {}", signal.senderId, authenticatedUserId)
            return
        }

        logger.info("[SIGNALING_RELAY] Relay {} from {} to {} for callId={}", signal.signalType, signal.senderId, signal.targetUserId, signal.callId)
        callSignalingService.dispatchSignalToUser(signal.targetUserId, signal)
    }

    /**
     * Returns ICE server configuration for WebRTC NAT traversal (STUN + optional TURN).
     */
    fun getIceServers(): CallIceServersResponse {
        val servers = mutableListOf(
            IceServerConfig(urls = listOf("stun:stun.l.google.com:19302")),
            IceServerConfig(urls = listOf("stun:stun1.l.google.com:19302")),
            IceServerConfig(urls = listOf("stun:stun2.l.google.com:19302"))
        )

        if (turnUrl.isNotBlank()) {
            servers.add(
                IceServerConfig(
                    urls = listOf(turnUrl),
                    username = turnUsername.takeIf { it.isNotBlank() },
                    credential = turnCredential.takeIf { it.isNotBlank() }
                )
            )
        }

        return CallIceServersResponse(iceServers = servers)
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

    fun getCallHistoryList(userId: String, limit: Int = 50): List<CallHistoryItemResponse> {
        return callRepository.findRecentCallsForUser(userId)
            .sortedByDescending { it.createdAt }
            .take(limit)
            .map { call ->
                val otherUserId = if (call.callerId == userId) call.calleeId else call.callerId
                val isIncoming = call.calleeId == userId
                val otherUser = userModuleApi.findUserById(otherUserId)?.toPublicProfile()

                CallHistoryItemResponse(
                    id = call.id ?: throw IllegalStateException("Call ID cannot be null"),
                    conversationId = call.conversationId,
                    callerId = call.callerId,
                    calleeId = call.calleeId,
                    callType = call.callType,
                    status = call.status,
                    startTime = call.startTime,
                    endTime = call.endTime,
                    duration = call.duration,
                    createdAt = call.createdAt,
                    updatedAt = call.updatedAt,
                    otherUser = otherUser,
                    isIncoming = isIncoming
                )
            }
    }

    fun getOngoingCalls(): List<CallResponse> {
        return callRepository.findOngoingCalls().map { it.toCallResponse() }
    }

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

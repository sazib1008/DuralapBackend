package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.Call
import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import com.example.duralap.database.repository.CallRepository
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class CallService(
    private val callRepository: CallRepository,
    private val conversationRepository: ConversationRepository,
    private val userRepository: UserRepository
) {

    /**
     * Initiate a new call (WhatsApp-like calling)
     */
    fun initiateCall(request: CallInitiateRequest): CallResponse {
        // Validate users exist
        if (!userRepository.existsById(request.callerId)) {
            throw IllegalArgumentException("Caller does not exist")
        }
        if (!userRepository.existsById(request.calleeId)) {
            throw IllegalArgumentException("Callee does not exist")
        }

        // Validate conversation exists
        if (!conversationRepository.existsById(request.conversationId)) {
            throw IllegalArgumentException("Conversation does not exist")
        }

        // Check if callee is already in a call
        val activeCalls = callRepository.findActiveCallsForUser(request.calleeId)
        if (activeCalls.isNotEmpty()) {
            // Create busy call record
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
            val savedCall = callRepository.save(busyCall)
            return savedCall.toCallResponse()
        }

        // Check if caller is already in a call
        val callerActiveCalls = callRepository.findActiveCallsForUser(request.callerId)
        if (callerActiveCalls.isNotEmpty()) {
            throw IllegalArgumentException("You are already in a call")
        }

        // Create new call
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

        val savedCall = callRepository.save(call)
        return savedCall.toCallResponse()
    }

    /**
     * Accept an incoming call
     */
    fun acceptCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        // Verify user is the callee
        if (call.calleeId != userId) {
            throw IllegalArgumentException("You are not the callee for this call")
        }

        // Check if call is still ringing
        if (call.status != CallStatus.RINGING) {
            throw IllegalArgumentException("Call is no longer ringing")
        }

        // Update call status to active
        val updatedCall = call.copy(
            status = CallStatus.ACTIVE,
            startTime = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedCall = callRepository.save(updatedCall)
        return savedCall.toCallResponse()
    }

    /**
     * Reject an incoming call
     */
    fun rejectCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        // Verify user is the callee
        if (call.calleeId != userId) {
            throw IllegalArgumentException("You are not the callee for this call")
        }

        // Check if call is still ringing
        if (call.status != CallStatus.RINGING) {
            throw IllegalArgumentException("Call is no longer ringing")
        }

        // Update call status to rejected
        val updatedCall = call.copy(
            status = CallStatus.REJECTED,
            endTime = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedCall = callRepository.save(updatedCall)
        return savedCall.toCallResponse()
    }

    /**
     * End a call
     */
    fun endCall(callId: String, userId: String): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        // Verify user is participant in the call
        if (call.callerId != userId && call.calleeId != userId) {
            throw IllegalArgumentException("You are not a participant in this call")
        }

        // Calculate duration if call was active
        val duration = if (call.startTime != null) {
            val endTime = Instant.now()
            java.time.Duration.between(call.startTime, endTime).seconds
        } else null

        // Update call status to ended
        val updatedCall = call.copy(
            status = CallStatus.ENDED,
            endTime = Instant.now(),
            duration = duration,
            updatedAt = Instant.now()
        )

        val savedCall = callRepository.save(updatedCall)
        return savedCall.toCallResponse()
    }

    /**
     * Get call by ID
     */
    fun getCallById(callId: String): CallResponse? {
        return callRepository.findByIdOrNull(callId)?.toCallResponse()
    }

    /**
     * Get active calls for user
     */
    fun getActiveCallsForUser(userId: String): List<CallResponse> {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User does not exist")
        }

        val calls = callRepository.findActiveCallsForUser(userId)
        return calls.map { it.toCallResponse() }
    }

    /**
     * Get recent calls for user
     */
    fun getRecentCallsForUser(userId: String, limit: Int = 20): List<CallResponse> {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User does not exist")
        }

        val calls = callRepository.findRecentCallsForUser(userId)
            .sortedByDescending { it.createdAt }
            .take(limit)
        
        return calls.map { it.toCallResponse() }
    }

    /**
     * Get missed calls for user
     */
    fun getMissedCallsForUser(userId: String): List<CallResponse> {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User does not exist")
        }

        val calls = callRepository.findMissedCallsForUser(userId)
        return calls.map { it.toCallResponse() }
    }

    /**
     * Get call history between two users
     */
    fun getCallHistory(user1Id: String, user2Id: String, limit: Int = 50): List<CallResponse> {
        if (!userRepository.existsById(user1Id) || !userRepository.existsById(user2Id)) {
            throw IllegalArgumentException("One or both users do not exist")
        }

        val calls = callRepository.findCallsBetweenUsers(user1Id, user2Id)
            .sortedByDescending { it.createdAt }
            .take(limit)
        
        return calls.map { it.toCallResponse() }
    }

    /**
     * Get ongoing calls (for admin/monitoring)
     */
    fun getOngoingCalls(): List<CallResponse> {
        val calls = callRepository.findOngoingCalls()
        return calls.map { it.toCallResponse() }
    }

    /**
     * Update call with WebRTC signaling data
     */
    fun updateCallWithSignaling(callId: String, offer: String? = null, answer: String? = null, iceCandidates: List<String>? = null): CallResponse {
        val call = callRepository.findByIdOrNull(callId)
            ?: throw IllegalArgumentException("Call not found")

        val updatedCall = call.copy(
            offer = offer ?: call.offer,
            answer = answer ?: call.answer,
            iceCandidates = iceCandidates ?: call.iceCandidates,
            updatedAt = Instant.now()
        )

        val savedCall = callRepository.save(updatedCall)
        return savedCall.toCallResponse()
    }

    /**
     * Get call statistics for user
     */
    fun getCallStats(userId: String): Map<String, Any> {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User does not exist")
        }

        return mapOf(
            "totalCalls" to callRepository.countCallsByStatusForUser(userId, CallStatus.ENDED),
            "missedCalls" to callRepository.countCallsByStatusForUser(userId, CallStatus.MISSED),
            "rejectedCalls" to callRepository.countCallsByStatusForUser(userId, CallStatus.REJECTED),
            "activeCalls" to callRepository.findActiveCallsForUser(userId).size,
            "audioCalls" to callRepository.findByCallType(CallType.AUDIO).count { it.callerId == userId || it.calleeId == userId },
            "videoCalls" to callRepository.findByCallType(CallType.VIDEO).count { it.callerId == userId || it.calleeId == userId }
        )
    }
}

/**
 * Extension function to convert Call to CallResponse
 */
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
package com.example.duralap.presence.application.service

import com.example.duralap.database.dto.CallInitiateRequest
import com.example.duralap.database.dto.CallSignalingMessage
import com.example.duralap.database.dto.SignalType
import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import com.example.duralap.presence.application.cache.CallAcquireResult
import com.example.duralap.presence.application.cache.CallAcquireStatus
import com.example.duralap.presence.application.cache.CallSessionRedisCache
import com.example.duralap.presence.application.signaling.CallSignalingService
import com.example.duralap.presence.domain.model.Call
import com.example.duralap.presence.domain.repository.CallRepository
import com.example.duralap.user.api.UserModuleApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class CallServiceTest {

    @Mock
    private lateinit var callRepository: CallRepository

    @Mock
    private lateinit var callSessionRedisCache: CallSessionRedisCache

    @Mock
    private lateinit var callSignalingService: CallSignalingService

    @Mock
    private lateinit var userModuleApi: UserModuleApi

    private lateinit var callService: CallService

    @BeforeEach
    fun setUp() {
        callService = CallService(
            callRepository = callRepository,
            callSessionRedisCache = callSessionRedisCache,
            callSignalingService = callSignalingService,
            userModuleApi = userModuleApi
        )
    }

    @Test
    fun `initiateCall successfully acquires session and dispatches CALL_INITIATE signal`() {
        val callerId = "user-alice"
        val calleeId = "user-bob"
        val convId = "conv-123"

        whenever(callSessionRedisCache.tryAcquireCall(any(), eq(callerId), eq(calleeId), eq(convId), eq(CallType.AUDIO), any()))
            .thenReturn(CallAcquireResult(CallAcquireStatus.SUCCESS))

        whenever(callRepository.save(any<Call>())).thenAnswer { invocation ->
            invocation.getArgument(0) as Call
        }

        val request = CallInitiateRequest(
            conversationId = convId,
            callerId = callerId,
            calleeId = calleeId,
            callType = CallType.AUDIO
        )

        val response = callService.initiateCall(request)

        assertThat(response.status).isEqualTo(CallStatus.RINGING)
        assertThat(response.callerId).isEqualTo(callerId)
        assertThat(response.calleeId).isEqualTo(calleeId)

        val signalCaptor = argumentCaptor<CallSignalingMessage>()
        verify(callSignalingService, atLeastOnce()).dispatchSignalToUser(eq(calleeId), signalCaptor.capture())
        assertThat(signalCaptor.allValues.any { it.signalType == SignalType.CALL_INITIATE }).isTrue()
    }

    @Test
    fun `initiateCall when callee is BUSY returns BUSY and dispatches CALL_BUSY signal`() {
        val callerId = "user-alice"
        val calleeId = "user-bob"
        val convId = "conv-123"

        whenever(callSessionRedisCache.tryAcquireCall(any(), eq(callerId), eq(calleeId), eq(convId), eq(CallType.VIDEO), any()))
            .thenReturn(CallAcquireResult(CallAcquireStatus.CALLEE_BUSY, "existing-call-456"))

        whenever(callRepository.save(any<Call>())).thenAnswer { invocation ->
            invocation.getArgument(0) as Call
        }

        val request = CallInitiateRequest(
            conversationId = convId,
            callerId = callerId,
            calleeId = calleeId,
            callType = CallType.VIDEO
        )

        val response = callService.initiateCall(request)

        assertThat(response.status).isEqualTo(CallStatus.BUSY)
        verify(callSignalingService).sendLifecycleSignal(
            any(),
            eq(calleeId),
            eq(callerId),
            eq(SignalType.CALL_BUSY),
            eq(CallType.VIDEO),
            eq(convId),
            eq("CALLEE_BUSY")
        )
    }

    @Test
    fun `acceptCall transitions call to CONNECTED and notifies caller`() {
        val callId = "call-1"
        val callerId = "user-alice"
        val calleeId = "user-bob"

        val call = Call(
            id = callId,
            conversationId = "conv-1",
            callerId = callerId,
            calleeId = calleeId,
            callType = CallType.AUDIO,
            status = CallStatus.RINGING
        )

        whenever(callRepository.findById(callId)).thenReturn(Optional.of(call))
        whenever(callRepository.save(any<Call>())).thenAnswer { invocation -> invocation.getArgument(0) }

        val response = callService.acceptCall(callId, calleeId)

        assertThat(response.status).isEqualTo(CallStatus.CONNECTED)
        assertThat(response.startTime).isNotNull()

        verify(callSessionRedisCache).updateCallStatus(callId, CallStatus.CONNECTED, true)
        verify(callSignalingService).sendLifecycleSignal(
            callId = eq(callId),
            senderId = eq(calleeId),
            targetUserId = eq(callerId),
            signalType = eq(SignalType.CALL_ACCEPT),
            callType = eq(CallType.AUDIO),
            conversationId = eq("conv-1"),
            reason = isNull()
        )
    }

    @Test
    fun `rejectCall marks call REJECTED and releases Redis lock`() {
        val callId = "call-1"
        val callerId = "user-alice"
        val calleeId = "user-bob"

        val call = Call(
            id = callId,
            conversationId = "conv-1",
            callerId = callerId,
            calleeId = calleeId,
            callType = CallType.AUDIO,
            status = CallStatus.RINGING
        )

        whenever(callRepository.findById(callId)).thenReturn(Optional.of(call))
        whenever(callRepository.save(any<Call>())).thenAnswer { invocation -> invocation.getArgument(0) }

        val response = callService.rejectCall(callId, calleeId, "USER_DECLINED")

        assertThat(response.status).isEqualTo(CallStatus.REJECTED)
        verify(callSessionRedisCache).releaseCall(callId, callerId, calleeId)
        verify(callSignalingService).sendLifecycleSignal(
            callId = eq(callId),
            senderId = eq(calleeId),
            targetUserId = eq(callerId),
            signalType = eq(SignalType.CALL_REJECT),
            callType = eq(CallType.AUDIO),
            conversationId = eq("conv-1"),
            reason = eq("USER_DECLINED")
        )
    }

    @Test
    fun `cancelCall by caller transitions to CANCELLED and notifies callee`() {
        val callId = "call-1"
        val callerId = "user-alice"
        val calleeId = "user-bob"

        val call = Call(
            id = callId,
            conversationId = "conv-1",
            callerId = callerId,
            calleeId = calleeId,
            callType = CallType.AUDIO,
            status = CallStatus.RINGING
        )

        whenever(callRepository.findById(callId)).thenReturn(Optional.of(call))
        whenever(callRepository.save(any<Call>())).thenAnswer { invocation -> invocation.getArgument(0) }

        val response = callService.cancelCall(callId, callerId)

        assertThat(response.status).isEqualTo(CallStatus.CANCELLED)
        verify(callSessionRedisCache).releaseCall(callId, callerId, calleeId)
        verify(callSignalingService).sendLifecycleSignal(
            callId = eq(callId),
            senderId = eq(callerId),
            targetUserId = eq(calleeId),
            signalType = eq(SignalType.CALL_CANCEL),
            callType = eq(CallType.AUDIO),
            conversationId = eq("conv-1"),
            reason = eq("CANCELLED_BY_CALLER")
        )
    }

    @Test
    fun `endCall calculates duration, saves ENDED, and releases Redis locks`() {
        val callId = "call-1"
        val callerId = "user-alice"
        val calleeId = "user-bob"
        val startTime = Instant.now().minusSeconds(125)

        val call = Call(
            id = callId,
            conversationId = "conv-1",
            callerId = callerId,
            calleeId = calleeId,
            callType = CallType.AUDIO,
            status = CallStatus.CONNECTED,
            startTime = startTime
        )

        whenever(callRepository.findById(callId)).thenReturn(Optional.of(call))
        whenever(callRepository.save(any<Call>())).thenAnswer { invocation -> invocation.getArgument(0) }

        val response = callService.endCall(callId, callerId)

        assertThat(response.status).isEqualTo(CallStatus.ENDED)
        assertThat(response.duration).isNotNull()
        assertThat(response.duration).isGreaterThanOrEqualTo(125L)

        verify(callSessionRedisCache).releaseCall(callId, callerId, calleeId)
        verify(callSignalingService).sendLifecycleSignal(
            callId = eq(callId),
            senderId = eq(callerId),
            targetUserId = eq(calleeId),
            signalType = eq(SignalType.CALL_END),
            callType = eq(CallType.AUDIO),
            conversationId = eq("conv-1"),
            reason = eq("NORMAL_HANGUP")
        )
    }

    @Test
    fun `getIceServers returns default Google STUN servers`() {
        val iceServersResponse = callService.getIceServers()

        assertThat(iceServersResponse.iceServers).isNotEmpty
        assertThat(iceServersResponse.iceServers.first().urls).contains("stun:stun.l.google.com:19302")
    }
}

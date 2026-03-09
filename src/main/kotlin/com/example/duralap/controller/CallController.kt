package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.service.CallService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(origins = ["http://localhost:3000"])
class CallController(
    private val callService: CallService
) {

    @PostMapping("/initiate")
    fun initiateCall(@Valid @RequestBody request: CallInitiateRequest): ResponseEntity<CallResponse> {
        val call = callService.initiateCall(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(call)
    }

    @PostMapping("/{callId}/accept")
    fun acceptCall(@PathVariable callId: String, @RequestParam userId: String): ResponseEntity<CallResponse> {
        val call = callService.acceptCall(callId, userId)
        return ResponseEntity.ok(call)
    }

    @PostMapping("/{callId}/reject")
    fun rejectCall(@PathVariable callId: String, @RequestParam userId: String): ResponseEntity<CallResponse> {
        val call = callService.rejectCall(callId, userId)
        return ResponseEntity.ok(call)
    }

    @PostMapping("/{callId}/end")
    fun endCall(@PathVariable callId: String, @RequestParam userId: String): ResponseEntity<CallResponse> {
        val call = callService.endCall(callId, userId)
        return ResponseEntity.ok(call)
    }

    @GetMapping("/{callId}")
    fun getCallById(@PathVariable callId: String): ResponseEntity<CallResponse> {
        val call = callService.getCallById(callId)
        return call?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/active/{userId}")
    fun getActiveCallsForUser(@PathVariable userId: String): ResponseEntity<List<CallResponse>> {
        val calls = callService.getActiveCallsForUser(userId)
        return ResponseEntity.ok(calls)
    }

    @GetMapping("/recent/{userId}")
    fun getRecentCallsForUser(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<List<CallResponse>> {
        val calls = callService.getRecentCallsForUser(userId, limit)
        return ResponseEntity.ok(calls)
    }

    @GetMapping("/missed/{userId}")
    fun getMissedCallsForUser(@PathVariable userId: String): ResponseEntity<List<CallResponse>> {
        val calls = callService.getMissedCallsForUser(userId)
        return ResponseEntity.ok(calls)
    }

    @GetMapping("/history")
    fun getCallHistory(
        @RequestParam user1Id: String,
        @RequestParam user2Id: String,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<List<CallResponse>> {
        val calls = callService.getCallHistory(user1Id, user2Id, limit)
        return ResponseEntity.ok(calls)
    }

    @GetMapping("/ongoing")
    fun getOngoingCalls(): ResponseEntity<List<CallResponse>> {
        val calls = callService.getOngoingCalls()
        return ResponseEntity.ok(calls)
    }

    @PatchMapping("/{callId}/signaling")
    fun updateCallWithSignaling(
        @PathVariable callId: String,
        @RequestParam(required = false) offer: String?,
        @RequestParam(required = false) answer: String?,
        @RequestParam(required = false) iceCandidates: List<String>?
    ): ResponseEntity<CallResponse> {
        val call = callService.updateCallWithSignaling(callId, offer, answer, iceCandidates)
        return ResponseEntity.ok(call)
    }

    @GetMapping("/stats/{userId}")
    fun getCallStats(@PathVariable userId: String): ResponseEntity<Map<String, Any>> {
        val stats = callService.getCallStats(userId)
        return ResponseEntity.ok(stats)
    }
}
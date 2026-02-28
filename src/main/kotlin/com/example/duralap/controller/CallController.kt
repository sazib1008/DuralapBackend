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

    /**
     * Initiate a new call
     */
    @PostMapping("/initiate")
    fun initiateCall(@Valid @RequestBody request: CallInitiateRequest): ResponseEntity<CallResponse> {
        return try {
            val call = callService.initiateCall(request)
            ResponseEntity.status(HttpStatus.CREATED).body(call)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Accept an incoming call
     */
    @PostMapping("/{callId}/accept")
    fun acceptCall(@PathVariable callId: String, @RequestParam userId: String): ResponseEntity<CallResponse> {
        return try {
            val call = callService.acceptCall(callId, userId)
            ResponseEntity.ok(call)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Reject an incoming call
     */
    @PostMapping("/{callId}/reject")
    fun rejectCall(@PathVariable callId: String, @RequestParam userId: String): ResponseEntity<CallResponse> {
        return try {
            val call = callService.rejectCall(callId, userId)
            ResponseEntity.ok(call)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * End a call
     */
    @PostMapping("/{callId}/end")
    fun endCall(@PathVariable callId: String, @RequestParam userId: String): ResponseEntity<CallResponse> {
        return try {
            val call = callService.endCall(callId, userId)
            ResponseEntity.ok(call)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get call by ID
     */
    @GetMapping("/{callId}")
    fun getCallById(@PathVariable callId: String): ResponseEntity<CallResponse> {
        val call = callService.getCallById(callId)
        return call?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Get active calls for user
     */
    @GetMapping("/active/{userId}")
    fun getActiveCallsForUser(@PathVariable userId: String): ResponseEntity<List<CallResponse>> {
        return try {
            val calls = callService.getActiveCallsForUser(userId)
            ResponseEntity.ok(calls)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get recent calls for user
     */
    @GetMapping("/recent/{userId}")
    fun getRecentCallsForUser(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<List<CallResponse>> {
        return try {
            val calls = callService.getRecentCallsForUser(userId, limit)
            ResponseEntity.ok(calls)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get missed calls for user
     */
    @GetMapping("/missed/{userId}")
    fun getMissedCallsForUser(@PathVariable userId: String): ResponseEntity<List<CallResponse>> {
        return try {
            val calls = callService.getMissedCallsForUser(userId)
            ResponseEntity.ok(calls)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get call history between two users
     */
    @GetMapping("/history")
    fun getCallHistory(
        @RequestParam user1Id: String,
        @RequestParam user2Id: String,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<List<CallResponse>> {
        return try {
            val calls = callService.getCallHistory(user1Id, user2Id, limit)
            ResponseEntity.ok(calls)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get ongoing calls (admin endpoint)
     */
    @GetMapping("/ongoing")
    fun getOngoingCalls(): ResponseEntity<List<CallResponse>> {
        val calls = callService.getOngoingCalls()
        return ResponseEntity.ok(calls)
    }

    /**
     * Update call with WebRTC signaling data
     */
    @PatchMapping("/{callId}/signaling")
    fun updateCallWithSignaling(
        @PathVariable callId: String,
        @RequestParam(required = false) offer: String?,
        @RequestParam(required = false) answer: String?,
        @RequestParam(required = false) iceCandidates: List<String>?
    ): ResponseEntity<CallResponse> {
        return try {
            val call = callService.updateCallWithSignaling(callId, offer, answer, iceCandidates)
            ResponseEntity.ok(call)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get call statistics for user
     */
    @GetMapping("/stats/{userId}")
    fun getCallStats(@PathVariable userId: String): ResponseEntity<Map<String, Any>> {
        return try {
            val stats = callService.getCallStats(userId)
            ResponseEntity.ok(stats)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}
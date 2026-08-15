package com.example.duralap.presence.web.controller

import com.example.duralap.database.dto.BatchPresenceRequest
import com.example.duralap.database.dto.PresenceHeartbeatRequest
import com.example.duralap.database.dto.UserPresenceResponse
import com.example.duralap.presence.application.service.PresenceService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/presence")
@CrossOrigin(origins = ["*"])
class PresenceController(
    private val presenceService: PresenceService
) {
    private val logger = LoggerFactory.getLogger(PresenceController::class.java)

    @GetMapping("/{userId}")
    fun getUserPresence(@PathVariable userId: String): ResponseEntity<UserPresenceResponse> {
        val presence = presenceService.getUserPresence(userId)
        return ResponseEntity.ok(presence)
    }

    @PostMapping("/batch")
    fun getBatchPresence(@RequestBody request: BatchPresenceRequest): ResponseEntity<List<UserPresenceResponse>> {
        val presences = presenceService.getUsersPresence(request.userIds)
        return ResponseEntity.ok(presences)
    }

    @GetMapping("/online/{userId}")
    fun checkIsUserOnline(@PathVariable userId: String): ResponseEntity<Map<String, Boolean>> {
        val isOnline = presenceService.isUserOnline(userId)
        return ResponseEntity.ok(mapOf("isOnline" to isOnline))
    }

    /**
     * STOMP Message handler for client heartbeats.
     * Clients send to /app/presence.heartbeat every 15s to maintain session liveness.
     */
    @MessageMapping("/presence.heartbeat")
    fun handleHeartbeat(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload(required = false) request: PresenceHeartbeatRequest?
    ) {
        val userId = headerAccessor.user?.name ?: headerAccessor.sessionAttributes?.get("userId") as? String
        val sessionId = headerAccessor.sessionId

        if (!userId.isNullOrBlank() && !sessionId.isNullOrBlank()) {
            presenceService.heartbeatSession(
                userId = userId,
                sessionId = sessionId,
                deviceId = request?.deviceId,
                clientType = request?.clientType ?: "ANDROID"
            )
        } else {
            logger.warn("Received heartbeat without valid userId or sessionId")
        }
    }
}

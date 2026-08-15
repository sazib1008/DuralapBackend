package com.example.duralap.database.dto

import com.example.duralap.database.model.UserStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class UserPresenceResponse(
    val userId: String,
    val status: UserStatus,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val lastSeen: Instant? = null,
    val sessionCount: Int = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val timestamp: Instant = Instant.now()
)

data class PresenceHeartbeatRequest(
    val deviceId: String? = null,
    val clientType: String? = "ANDROID"
)

data class BatchPresenceRequest(
    val userIds: List<String> = emptyList()
)

package com.example.duralap.events

import com.example.duralap.database.model.UserStatus
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class UserPresenceChangedEvent(
    val userId: String,
    val status: UserStatus,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val lastSeen: Instant? = null,
    val sessionCount: Int = 0,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val timestamp: Instant = Instant.now()
)

package com.example.duralap.events

import java.time.Instant

data class MediaDeletedEvent(
    val mediaId: String,
    val storagePath: String,
    val timestamp: Instant = Instant.now()
)

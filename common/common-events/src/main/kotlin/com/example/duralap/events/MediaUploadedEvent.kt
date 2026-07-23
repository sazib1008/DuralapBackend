package com.example.duralap.events

import java.time.Instant

data class MediaUploadedEvent(
    val mediaId: String,
    val ownerId: String,
    val conversationId: String?,
    val storagePath: String,
    val type: String,
    val timestamp: Instant = Instant.now()
)

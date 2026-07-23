package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "media_files")
data class MediaFile(
    @Id
    val id: String? = null,
    val ownerId: String,
    val conversationId: String? = null,
    val messageId: String? = null,
    val type: String,                 // IMAGE, VIDEO, VOICE_NOTE, AUDIO, DOCUMENT, GIF, STICKER
    val originalFileName: String,
    val storagePath: String,
    val mimeType: String,
    val size: Long,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Long? = null,       // Duration in seconds for video/audio
    val status: String = "READY",     // UPLOADING, UPLOADED, PROCESSING, READY, FAILED, DELETED
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

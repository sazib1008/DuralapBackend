package com.example.duralap.media.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("media_files")
@CompoundIndex(name = "owner_created_idx", def = "{'ownerId': 1, 'createdAt': -1}")
data class MediaFile(
    @Id
    val id: String? = null,

    @Indexed
    val ownerId: String,

    val conversationId: String? = null,

    val messageId: String? = null,

    val type: String,

    val originalFileName: String,

    val storagePath: String,

    val mimeType: String,

    val size: Long,

    val status: String = "READY",

    val createdAt: Instant = Instant.now(),

    val updatedAt: Instant = Instant.now()
)

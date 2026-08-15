package com.example.duralap.media.application.service

import com.example.duralap.events.MediaDeletedEvent
import com.example.duralap.events.MediaUploadedEvent
import com.example.duralap.media.domain.model.MediaFile
import com.example.duralap.media.domain.repository.MediaFileRepository
import com.example.duralap.media.infrastructure.external.SupabaseStorageService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.UUID

@Service
class MediaService(
    private val mediaFileRepository: MediaFileRepository,
    private val supabaseStorageService: SupabaseStorageService,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun uploadMedia(
        ownerId: String,
        file: MultipartFile,
        conversationId: String?,
        messageId: String?
    ): MediaFile {
        val originalFileName = file.originalFilename ?: "unknown"
        val mimeType = file.contentType ?: "application/octet-stream"
        val size = file.size

        val mediaType = getMediaType(mimeType)
        validateSize(mediaType, size)

        val extension = originalFileName.substringAfterLast('.', "")
        val uniqueName = "${UUID.randomUUID()}.${if (extension.isNotEmpty()) extension else "bin"}"
        val storagePath = "$mediaType/$uniqueName"

        supabaseStorageService.uploadFile(storagePath, file.bytes, mimeType)

        val mediaFile = MediaFile(
            ownerId = ownerId,
            conversationId = conversationId,
            messageId = messageId,
            type = mediaType,
            originalFileName = originalFileName,
            storagePath = storagePath,
            mimeType = mimeType,
            size = size,
            status = "READY",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        val saved = mediaFileRepository.save(mediaFile)

        eventPublisher.publishEvent(
            MediaUploadedEvent(
                mediaId = saved.id!!,
                ownerId = ownerId,
                conversationId = conversationId,
                storagePath = storagePath,
                type = mediaType
            )
        )

        return saved
    }

    fun getMediaById(id: String): MediaFile? {
        return mediaFileRepository.findById(id).orElse(null)
    }

    fun generateDownloadUrl(id: String): String {
        val media = getMediaById(id) ?: throw NoSuchElementException("Media file not found with ID $id")
        return supabaseStorageService.getSignedUrl(media.storagePath)
    }

    fun deleteMedia(id: String, ownerId: String) {
        val media = getMediaById(id) ?: throw NoSuchElementException("Media file not found with ID $id")
        if (media.ownerId != ownerId) {
            throw IllegalArgumentException("Not authorized to delete this media file")
        }

        supabaseStorageService.deleteFile(media.storagePath)
        mediaFileRepository.deleteById(id)

        eventPublisher.publishEvent(
            MediaDeletedEvent(
                mediaId = id,
                storagePath = media.storagePath
            )
        )
    }

    private fun getMediaType(mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> "IMAGE"
            mimeType.startsWith("video/") -> "VIDEO"
            mimeType.startsWith("audio/") -> "AUDIO"
            mimeType.contains("pdf") || mimeType.contains("word") || mimeType.contains("excel") || 
                    mimeType.contains("powerpoint") || mimeType.startsWith("text/") || 
                    mimeType.contains("octet-stream") -> "DOCUMENT"
            else -> "DOCUMENT"
        }
    }

    private fun validateSize(mediaType: String, size: Long) {
        val limits = mapOf(
            "IMAGE" to 20 * 1024 * 1024L,       // 20MB
            "VIDEO" to 500 * 1024 * 1024L,     // 500MB
            "AUDIO" to 100 * 1024 * 1024L,     // 100MB
            "DOCUMENT" to 100 * 1024 * 1024L   // 100MB
        )
        val limit = limits[mediaType] ?: (100 * 1024 * 1024L)
        if (size > limit) {
            throw IllegalArgumentException("File size exceeds limit of ${limit / (1024 * 1024)}MB for type $mediaType")
        }
    }
}

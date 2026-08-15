package com.example.duralap.media.web.controller

import com.example.duralap.media.domain.model.MediaFile
import com.example.duralap.media.application.service.MediaService
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = ["*"])
class MediaController(
    private val mediaService: MediaService,
    private val userRepository: UserRepository
) {

    @PostMapping("/upload")
    fun uploadMedia(
        @RequestParam("file") file: MultipartFile,
        @RequestParam(required = false) conversationId: String?,
        @RequestParam(required = false) messageId: String?
    ): ResponseEntity<MediaFile> {
        val currentUsername = AuthenticatedUserUtil.getCurrentUsername()
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }
            
        val result = mediaService.uploadMedia(currentUser.id!!, file, conversationId, messageId)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @GetMapping("/{id}")
    fun getMediaMetadata(@PathVariable id: String): ResponseEntity<MediaFile> {
        val media = mediaService.getMediaById(id)
            ?: throw NoSuchElementException("Media file not found with ID $id")
        return ResponseEntity.ok(media)
    }

    @GetMapping("/{id}/download")
    fun downloadMedia(@PathVariable id: String): ResponseEntity<Map<String, String>> {
        val url = mediaService.generateDownloadUrl(id)
        return ResponseEntity.ok(mapOf("url" to url))
    }

    @DeleteMapping("/{id}")
    fun deleteMedia(@PathVariable id: String): ResponseEntity<Unit> {
        val currentUsername = AuthenticatedUserUtil.getCurrentUsername()
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        mediaService.deleteMedia(id, currentUser.id!!)
        return ResponseEntity.noContent().build()
    }
}

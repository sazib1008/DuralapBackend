package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.service.MessageService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import com.example.duralap.database.repository.UserRepository
import com.example.duralap.service.ConversationService
import org.springframework.security.core.context.SecurityContextHolder

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = ["http://localhost:3000"])
class MessageController(
    private val messageService: MessageService,
    private val userRepository: UserRepository,
    private val conversationService: ConversationService
) {

    @PostMapping
    fun sendMessage(@Valid @RequestBody request: MessageCreateRequest): ResponseEntity<MessageResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        if (request.senderId != currentUser.id) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val message = messageService.sendMessage(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(message)
    }

    @GetMapping("/conversation/{conversationId}")
    fun getMessages(
        @PathVariable conversationId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<MessageResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        if (!conversationService.isUserParticipant(conversationId, currentUser.id!!)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val messages = messageService.getMessages(conversationId, page, size)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/conversation/{conversationId}/all")
    fun getAllMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        if (!conversationService.isUserParticipant(conversationId, currentUser.id!!)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val messages = messageService.getAllMessages(conversationId)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/{id}")
    fun getMessageById(@PathVariable id: String): ResponseEntity<MessageResponse> {
        val message = messageService.getMessageById(id)
        return message?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PatchMapping("/{id}/read")
    fun markMessageAsRead(@PathVariable id: String): ResponseEntity<MessageResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        val message = messageService.markMessageAsRead(id, currentUser.id!!)
        return ResponseEntity.ok(message)
    }

    @PatchMapping("/conversation/{conversationId}/mark-all-read")
    fun markAllMessagesAsRead(@PathVariable conversationId: String): ResponseEntity<Unit> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        messageService.markAllMessagesAsRead(conversationId, currentUser.id!!)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/conversation/{conversationId}/unread-count")
    fun getUnreadMessagesCount(@PathVariable conversationId: String): ResponseEntity<Long> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        val count = messageService.getUnreadMessagesCount(conversationId, currentUser.id!!)
        return ResponseEntity.ok(count)
    }

    @GetMapping("/conversation/{conversationId}/unread")
    fun getUnreadMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        val messages = messageService.getUnreadMessages(conversationId, currentUser.id!!)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/conversation/{conversationId}/last")
    fun getLastMessage(@PathVariable conversationId: String): ResponseEntity<MessageResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        if (!conversationService.isUserParticipant(conversationId, currentUser.id!!)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val message = messageService.getLastMessage(conversationId)
        return message?.let { ResponseEntity.ok(it) } ?: ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun deleteMessage(@PathVariable id: String): ResponseEntity<Boolean> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        val result = messageService.deleteMessage(id, currentUser.id!!)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/conversation/{conversationId}/type/{messageType}")
    fun getMessagesByType(
        @PathVariable conversationId: String,
        @PathVariable messageType: com.example.duralap.database.model.MessageType
    ): ResponseEntity<List<MessageResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        if (!conversationService.isUserParticipant(conversationId, currentUser.id!!)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val messages = messageService.getMessagesByType(conversationId, messageType)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/conversation/{conversationId}/media")
    fun getMediaMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUsername = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        if (!conversationService.isUserParticipant(conversationId, currentUser.id!!)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val messages = messageService.getMediaMessages(conversationId)
        return ResponseEntity.ok(messages)
    }
}
package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.service.MessageService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = ["http://localhost:3000"])
class MessageController(
    private val messageService: MessageService
) {

    @PostMapping
    fun sendMessage(@Valid @RequestBody request: MessageCreateRequest): ResponseEntity<MessageResponse> {
        val message = messageService.sendMessage(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(message)
    }

    @GetMapping("/conversation/{conversationId}")
    fun getMessages(
        @PathVariable conversationId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<MessageResponse>> {
        val messages = messageService.getMessages(conversationId, page, size)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/conversation/{conversationId}/all")
    fun getAllMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
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
    fun markMessageAsRead(@PathVariable id: String, @RequestParam userId: String): ResponseEntity<MessageResponse> {
        val message = messageService.markMessageAsRead(id, userId)
        return ResponseEntity.ok(message)
    }

    @PatchMapping("/conversation/{conversationId}/mark-all-read")
    fun markAllMessagesAsRead(@PathVariable conversationId: String, @RequestParam userId: String): ResponseEntity<Unit> {
        messageService.markAllMessagesAsRead(conversationId, userId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/conversation/{conversationId}/unread-count")
    fun getUnreadMessagesCount(@PathVariable conversationId: String, @RequestParam userId: String): ResponseEntity<Long> {
        val count = messageService.getUnreadMessagesCount(conversationId, userId)
        return ResponseEntity.ok(count)
    }

    @GetMapping("/conversation/{conversationId}/unread")
    fun getUnreadMessages(@PathVariable conversationId: String, @RequestParam userId: String): ResponseEntity<List<MessageResponse>> {
        val messages = messageService.getUnreadMessages(conversationId, userId)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/conversation/{conversationId}/last")
    fun getLastMessage(@PathVariable conversationId: String): ResponseEntity<MessageResponse> {
        val message = messageService.getLastMessage(conversationId)
        return message?.let { ResponseEntity.ok(it) } ?: ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun deleteMessage(@PathVariable id: String, @RequestParam userId: String): ResponseEntity<Boolean> {
        val result = messageService.deleteMessage(id, userId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/conversation/{conversationId}/type/{messageType}")
    fun getMessagesByType(
        @PathVariable conversationId: String,
        @PathVariable messageType: com.example.duralap.database.model.MessageType
    ): ResponseEntity<List<MessageResponse>> {
        val messages = messageService.getMessagesByType(conversationId, messageType)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/conversation/{conversationId}/media")
    fun getMediaMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
        val messages = messageService.getMediaMessages(conversationId)
        return ResponseEntity.ok(messages)
    }
}
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

    /**
     * Send a new message
     */
    @PostMapping
    fun sendMessage(@Valid @RequestBody request: MessageCreateRequest): ResponseEntity<MessageResponse> {
        return try {
            val message = messageService.sendMessage(request)
            ResponseEntity.status(HttpStatus.CREATED).body(message)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get messages for a conversation
     */
    @GetMapping("/conversation/{conversationId}")
    fun getMessages(
        @PathVariable conversationId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<MessageResponse>> {
        return try {
            val messages = messageService.getMessages(conversationId, page, size)
            ResponseEntity.ok(messages)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get all messages for a conversation (without pagination)
     */
    @GetMapping("/conversation/{conversationId}/all")
    fun getAllMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
        return try {
            val messages = messageService.getAllMessages(conversationId)
            ResponseEntity.ok(messages)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get message by ID
     */
    @GetMapping("/{id}")
    fun getMessageById(@PathVariable id: String): ResponseEntity<MessageResponse> {
        val message = messageService.getMessageById(id)
        return message?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Mark message as read
     */
    @PatchMapping("/{id}/read")
    fun markMessageAsRead(@PathVariable id: String, @RequestParam userId: String): ResponseEntity<MessageResponse> {
        return try {
            val message = messageService.markMessageAsRead(id, userId)
            ResponseEntity.ok(message)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Mark all messages in conversation as read
     */
    @PatchMapping("/conversation/{conversationId}/mark-all-read")
    fun markAllMessagesAsRead(@PathVariable conversationId: String, @RequestParam userId: String): ResponseEntity<Unit> {
        return try {
            messageService.markAllMessagesAsRead(conversationId, userId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get unread messages count for a user in a conversation
     */
    @GetMapping("/conversation/{conversationId}/unread-count")
    fun getUnreadMessagesCount(@PathVariable conversationId: String, @RequestParam userId: String): ResponseEntity<Long> {
        return try {
            val count = messageService.getUnreadMessagesCount(conversationId, userId)
            ResponseEntity.ok(count)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get unread messages for a user in a conversation
     */
    @GetMapping("/conversation/{conversationId}/unread")
    fun getUnreadMessages(@PathVariable conversationId: String, @RequestParam userId: String): ResponseEntity<List<MessageResponse>> {
        return try {
            val messages = messageService.getUnreadMessages(conversationId, userId)
            ResponseEntity.ok(messages)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get last message in a conversation
     */
    @GetMapping("/conversation/{conversationId}/last")
    fun getLastMessage(@PathVariable conversationId: String): ResponseEntity<MessageResponse> {
        return try {
            val message = messageService.getLastMessage(conversationId)
            message?.let { ResponseEntity.ok(it) } ?: ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Delete message
     */
    @DeleteMapping("/{id}")
    fun deleteMessage(@PathVariable id: String, @RequestParam userId: String): ResponseEntity<Boolean> {
        return try {
            val result = messageService.deleteMessage(id, userId)
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get messages by type in a conversation
     */
    @GetMapping("/conversation/{conversationId}/type/{messageType}")
    fun getMessagesByType(
        @PathVariable conversationId: String,
        @PathVariable messageType: com.example.duralap.database.model.MessageType
    ): ResponseEntity<List<MessageResponse>> {
        return try {
            val messages = messageService.getMessagesByType(conversationId, messageType)
            ResponseEntity.ok(messages)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get media messages in a conversation
     */
    @GetMapping("/conversation/{conversationId}/media")
    fun getMediaMessages(@PathVariable conversationId: String): ResponseEntity<List<MessageResponse>> {
        return try {
            val messages = messageService.getMediaMessages(conversationId)
            ResponseEntity.ok(messages)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }
}
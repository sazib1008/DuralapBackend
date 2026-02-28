package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.service.ConversationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = ["http://localhost:3000"])
class ConversationController(
    private val conversationService: ConversationService
) {

    /**
     * Create a new conversation
     */
    @PostMapping
    fun createConversation(@Valid @RequestBody request: ConversationCreateRequest): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.createConversation(request)
            ResponseEntity.status(HttpStatus.CREATED).body(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get or create conversation between two users
     */
    @PostMapping("/get-or-create")
    fun getOrCreateConversation(@Valid @RequestBody request: GetOrCreateConversationRequest): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.getOrCreateConversation(request.user1Id, request.user2Id)
            ResponseEntity.ok(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get conversation by ID
     */
    @GetMapping("/{id}")
    fun getConversationById(@PathVariable id: String): ResponseEntity<ConversationResponse> {
        val conversation = conversationService.getConversationById(id)
        return conversation?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Get all conversations for a user
     */
    @GetMapping("/user/{userId}")
    fun getConversationsForUser(@PathVariable userId: String): ResponseEntity<List<ConversationResponse>> {
        return try {
            val conversations = conversationService.getConversationsForUser(userId)
            ResponseEntity.ok(conversations)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Delete conversation
     */
    @DeleteMapping("/{id}")
    fun deleteConversation(@PathVariable id: String): ResponseEntity<Unit> {
        return try {
            conversationService.deleteConversation(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Add participant to conversation
     */
    @PostMapping("/{conversationId}/participants/{userId}")
    fun addParticipant(@PathVariable conversationId: String, @PathVariable userId: String): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.addParticipant(conversationId, userId)
            ResponseEntity.ok(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Remove participant from conversation
     */
    @DeleteMapping("/{conversationId}/participants/{userId}")
    fun removeParticipant(@PathVariable conversationId: String, @PathVariable userId: String): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.removeParticipant(conversationId, userId)
            ResponseEntity.ok(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    /**
     * Get conversation participants
     */
    @GetMapping("/{conversationId}/participants")
    fun getConversationParticipants(@PathVariable conversationId: String): ResponseEntity<List<UserInfo>> {
        return try {
            val participants = conversationService.getConversationParticipants(conversationId)
            ResponseEntity.ok(participants)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}
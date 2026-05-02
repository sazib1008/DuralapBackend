package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.service.ConversationRequestService
import com.example.duralap.service.ConversationService
import com.example.duralap.service.MessageService
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.database.model.MessageType
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = ["http://localhost:3000"])
class ConversationController(
    private val conversationService: ConversationService,
    private val messageService: MessageService,
    private val userRepository: UserRepository,
    private val conversationRequestService: ConversationRequestService
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
     * Start conversation with a user by ID (creates a conversation request)
     * This is the NEW WhatsApp-like flow:
     * - If conversation exists and is accepted, return it
     * - If pending request exists, return it
     * - Otherwise, create a new PENDING conversation request
     */
    @PostMapping("/start-with")
    fun startConversationWithUser(@Valid @RequestBody request: StartConversationRequest): ResponseEntity<Any> {
        return try {
            val currentUserId = AuthenticatedUserUtil.getCurrentUserId(userRepository)
            
            val targetUser = userRepository.findById(request.targetUserId)
                .orElseThrow { IllegalArgumentException("Target user not found") }

            // Create or get conversation request
            val conversationRequest = conversationRequestService.createConversationRequest(
                senderId = currentUserId,
                recipientId = targetUser.id!!,
                initialMessage = request.initialMessage
            )
            
            ResponseEntity.ok(conversationRequest)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }



    /**
     * Get conversation by ID
     */
    @GetMapping("/{id}")
    fun getConversationById(@PathVariable id: String): ResponseEntity<ConversationResponse> {
        val currentUserId = AuthenticatedUserUtil.getCurrentUserId(userRepository)

        if (!conversationService.isUserParticipant(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val conversation = conversationService.getConversationById(id)
        return conversation?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Get all conversations for the authenticated user
     */
    @GetMapping("/my")
    fun getMyConversations(): ResponseEntity<List<ConversationResponse>> {
        val currentUserId = AuthenticatedUserUtil.getCurrentUserId(userRepository)

        val conversations = conversationService.getConversationsForUser(currentUserId)
        return ResponseEntity.ok(conversations)
    }

    /**
     * Get all conversations for a user (deprecated in favor of /my, but keeping for compatibility with check)
     */
    @GetMapping("/user/{userId}")
    fun getConversationsForUser(@PathVariable userId: String): ResponseEntity<List<ConversationResponse>> {
        val currentUserId = AuthenticatedUserUtil.getCurrentUserId(userRepository)

        if (currentUserId != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

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
        val currentUserId = AuthenticatedUserUtil.getCurrentUserId(userRepository)

        if (!conversationService.isUserParticipant(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

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
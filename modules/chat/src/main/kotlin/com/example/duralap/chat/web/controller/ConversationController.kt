package com.example.duralap.chat.web.controller

import com.example.duralap.chat.application.service.ConversationRequestService
import com.example.duralap.chat.application.service.ConversationService
import com.example.duralap.database.dto.*
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.user.domain.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = ["*"])
class ConversationController(
    private val conversationService: ConversationService,
    private val userRepository: UserRepository,
    private val conversationRequestService: ConversationRequestService
) {

    private fun getCurrentUserId(): String {
        return AuthenticatedUserUtil.getCurrentUserId { username ->
            userRepository.findByUsername(username).orElse(null)?.id
        }
    }

    @PostMapping
    fun createConversation(@Valid @RequestBody request: ConversationCreateRequest): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.createConversation(request)
            ResponseEntity.status(HttpStatus.CREATED).body(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/get-or-create")
    fun getOrCreateConversation(@Valid @RequestBody request: GetOrCreateConversationRequest): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.getOrCreateConversation(request.user1Id, request.user2Id)
            ResponseEntity.ok(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/start-with")
    fun startConversationWithUser(@Valid @RequestBody request: StartConversationRequest): ResponseEntity<Any> {
        return try {
            val currentUserId = getCurrentUserId()
            val targetUser = userRepository.findById(request.targetUserId)
                .orElseThrow { IllegalArgumentException("Target user not found") }

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

    @GetMapping("/{id}")
    fun getConversationById(@PathVariable id: String): ResponseEntity<ConversationResponse> {
        val currentUserId = getCurrentUserId()

        if (!conversationService.isUserParticipant(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val conversation = conversationService.getConversationById(id)
        return conversation?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/my")
    fun getMyConversations(): ResponseEntity<List<ConversationResponse>> {
        val currentUserId = getCurrentUserId()
        val conversations = conversationService.getConversationsForUser(currentUserId)
        return ResponseEntity.ok(conversations)
    }

    @GetMapping("/user/{userId}")
    fun getConversationsForUser(@PathVariable userId: String): ResponseEntity<List<ConversationResponse>> {
        val currentUserId = getCurrentUserId()

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

    @DeleteMapping("/{id}")
    fun deleteConversation(@PathVariable id: String): ResponseEntity<Unit> {
        val currentUserId = getCurrentUserId()

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

    @PostMapping("/{conversationId}/participants/{userId}")
    fun addParticipant(@PathVariable conversationId: String, @PathVariable userId: String): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.addParticipant(conversationId, userId)
            ResponseEntity.ok(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{conversationId}/participants/{userId}")
    fun removeParticipant(@PathVariable conversationId: String, @PathVariable userId: String): ResponseEntity<ConversationResponse> {
        return try {
            val conversation = conversationService.removeParticipant(conversationId, userId)
            ResponseEntity.ok(conversation)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

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

package com.example.duralap.controller

import com.example.duralap.database.dto.ConversationActionRequest
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.service.ConversationRequestService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
@RequestMapping("/api/conversation-requests")
@CrossOrigin(origins = ["http://localhost:3000"])
class ConversationRequestController(
    private val conversationRequestService: ConversationRequestService,
    private val userRepository: UserRepository
) {

    /**
     * Get all pending conversation requests for the authenticated user
     */
    @GetMapping("/pending")
    fun getPendingRequests(): ResponseEntity<Any> {
        return try {
            val userId = getCurrentUserId()
            val pendingRequests = conversationRequestService.getPendingRequestsForUser(userId)
            ResponseEntity.ok(pendingRequests)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Get count of pending conversation requests
     */
    @GetMapping("/pending/count")
    fun getPendingRequestCount(): ResponseEntity<Any> {
        return try {
            val userId = getCurrentUserId()
            val count = conversationRequestService.getPendingRequestCount(userId)
            ResponseEntity.ok(mapOf("count" to count))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Accept a conversation request
     */
    @PostMapping("/accept")
    fun acceptRequest(@Valid @RequestBody request: ConversationActionRequest): ResponseEntity<Any> {
        return try {
            val userId = getCurrentUserId()
            val result = conversationRequestService.acceptConversationRequest(
                request.conversationRequestId,
                userId
            )
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Reject a conversation request
     */
    @PostMapping("/reject")
    fun rejectRequest(@Valid @RequestBody request: ConversationActionRequest): ResponseEntity<Any> {
        return try {
            val userId = getCurrentUserId()
            val result = conversationRequestService.rejectConversationRequest(
                request.conversationRequestId,
                userId
            )
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Cancel a sent conversation request
     */
    @PostMapping("/cancel")
    fun cancelRequest(@Valid @RequestBody request: ConversationActionRequest): ResponseEntity<Any> {
        return try {
            val userId = getCurrentUserId()
            conversationRequestService.cancelConversationRequest(
                request.conversationRequestId,
                userId
            )
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    /**
     * Helper function to get current user ID from security context
     */
    private fun getCurrentUserId(): String {
        return AuthenticatedUserUtil.getCurrentUserId(userRepository)
    }
}

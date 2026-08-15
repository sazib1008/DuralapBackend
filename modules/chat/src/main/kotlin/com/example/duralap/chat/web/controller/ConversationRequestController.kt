package com.example.duralap.chat.web.controller

import com.example.duralap.chat.application.service.ConversationRequestService
import com.example.duralap.database.dto.ConversationActionRequest
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.user.domain.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
@RequestMapping("/api/conversation-requests")
@CrossOrigin(origins = ["*"])
class ConversationRequestController(
    private val conversationRequestService: ConversationRequestService,
    private val userRepository: UserRepository
) {

    private fun getCurrentUserId(): String {
        return AuthenticatedUserUtil.getCurrentUserId { username ->
            userRepository.findByUsername(username).orElse(null)?.id
        }
    }

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
}

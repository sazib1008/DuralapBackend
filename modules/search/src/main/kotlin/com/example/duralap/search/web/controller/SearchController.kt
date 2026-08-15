package com.example.duralap.search.web.controller

import com.example.duralap.chat.domain.model.Conversation
import com.example.duralap.database.dto.UserResponse
import com.example.duralap.search.application.service.SearchService
import com.example.duralap.search.dto.SearchResponse
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.user.domain.model.toUserResponse
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = ["*"])
class SearchController(
    private val searchService: SearchService,
    private val userRepository: UserRepository
) {

    @GetMapping("/users")
    fun searchUsers(
        @RequestParam q: String,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<SearchResponse<UserResponse>> {
        if (q.isBlank()) {
            return ResponseEntity.ok(SearchResponse(emptyList(), null))
        }
        val (users, nextCursor) = searchService.searchUsers(q, cursor, limit)
        val userResponses = users.map { it.toUserResponse() }
        return ResponseEntity.ok(SearchResponse(userResponses, nextCursor))
    }

    @GetMapping("/conversations")
    fun searchConversations(
        @RequestParam q: String,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<SearchResponse<Conversation>> {
        val currentUsername = AuthenticatedUserUtil.getCurrentUsername()
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current authenticated user not found") }
        
        if (q.isBlank()) {
            return ResponseEntity.ok(SearchResponse(emptyList(), null))
        }
        
        val (conversations, nextCursor) = searchService.searchConversations(currentUser.id!!, q, cursor, limit)
        return ResponseEntity.ok(SearchResponse(conversations, nextCursor))
    }
}

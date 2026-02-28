package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.UserStatus
import com.example.duralap.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["http://localhost:3000"]) // Adjust for your frontend
class UserController(
    private val userService: UserService
) {

    /**
     * Create a new user
     */
    @PostMapping
    fun createUser(@Valid @RequestBody request: UserCreateRequest): ResponseEntity<UserResponse> {
        return try {
            val user = userService.createUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserResponse> {
        val user = userService.getUserById(id)
        return user?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByUsername(username)
        return user?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByEmail(email)
        return user?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<UserResponse> {
        return try {
            val user = userService.updateUser(id, request)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<Unit> {
        return try {
            userService.deleteUser(id)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Get all users
     */
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    fun searchUsers(@RequestParam searchTerm: String): ResponseEntity<List<UserResponse>> {
        val users = userService.searchUsers(searchTerm)
        return ResponseEntity.ok(users)
    }

    /**
     * Get online users
     */
    @GetMapping("/online")
    fun getOnlineUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getOnlineUsers()
        return ResponseEntity.ok(users)
    }

    /**
     * Get available online users (not in call)
     */
    @GetMapping("/available")
    fun getAvailableOnlineUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.getAvailableOnlineUsers()
        return ResponseEntity.ok(users)
    }

    /**
     * Get users in call
     */
    @GetMapping("/in-call")
    fun getUsersInCall(): ResponseEntity<List<UserResponse>> {
        val users = userService.getUsersInCall()
        return ResponseEntity.ok(users)
    }

    /**
     * Update user status
     */
    @PatchMapping("/{id}/status")
    fun updateUserStatus(
        @PathVariable id: String,
        @RequestParam status: UserStatus
    ): ResponseEntity<UserResponse> {
        return try {
            val user = userService.updateUserStatus(id, status)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Update call status
     */
    @PatchMapping("/{id}/call-status")
    fun updateCallStatus(
        @PathVariable id: String,
        @RequestParam isInCall: Boolean,
        @RequestParam(required = false) callId: String?
    ): ResponseEntity<UserResponse> {
        return try {
            val user = userService.updateCallStatus(id, isInCall, callId)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Verify user email
     */
    @PatchMapping("/{id}/verify")
    fun verifyUserEmail(@PathVariable id: String): ResponseEntity<UserResponse> {
        return try {
            val user = userService.verifyUserEmail(id)
            ResponseEntity.ok(user)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Check if username exists
     */
    @GetMapping("/check-username/{username}")
    fun checkUsername(@PathVariable username: String): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.usernameExists(username)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Check if email exists
     */
    @GetMapping("/check-email/{email}")
    fun checkEmail(@PathVariable email: String): ResponseEntity<Map<String, Boolean>> {
        val exists = userService.emailExists(email)
        return ResponseEntity.ok(mapOf("exists" to exists))
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    fun getUserStats(): ResponseEntity<Map<String, Any>> {
        val stats = userService.getUserStats()
        return ResponseEntity.ok(stats)
    }
}
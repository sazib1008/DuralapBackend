package com.example.duralap.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.User
import com.example.duralap.database.model.UserStatus
import com.example.duralap.database.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Create a new user
     */
    fun createUser(request: UserCreateRequest): UserResponse {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            id = UUID.randomUUID().toString(),
            username = request.username.lowercase(),
            email = request.email.lowercase(),
            password = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            bio = request.bio,
            phoneNumber = request.phoneNumber,
            roles = request.roles,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(user)
        return savedUser.toUserResponse()
    }

    /**
     * Get user by ID
     */
    fun getUserById(id: String): UserResponse? {
        return userRepository.findByIdOrNull(id)?.toUserResponse()
    }

    /**
     * Get user by username
     */
    fun getUserByUsername(username: String): UserResponse? {
        return userRepository.findByUsername(username).orElse(null)?.toUserResponse()
    }

    /**
     * Get user by email
     */
    fun getUserByEmail(email: String): UserResponse? {
        return userRepository.findByEmail(email).orElse(null)?.toUserResponse()
    }

    /**
     * Update user
     */
    fun updateUser(id: String, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("User not found")

        val updatedUser = user.copy(
            fullName = request.fullName ?: user.fullName,
            bio = request.bio ?: user.bio,
            profileImageUrl = request.profileImageUrl ?: user.profileImageUrl,
            phoneNumber = request.phoneNumber ?: user.phoneNumber,
            status = request.status ?: user.status,
            isVerified = request.isVerified ?: user.isVerified,
            roles = request.roles ?: user.roles,
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return savedUser.toUserResponse()
    }

    /**
     * Delete user
     */
    fun deleteUser(id: String) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User not found")
        }
        userRepository.deleteById(id)
    }

    /**
     * Get all users
     */
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toUserResponse() }
    }

    /**
     * Search users by username or full name
     */
    fun searchUsers(searchTerm: String): List<UserResponse> {
        return userRepository.searchByUsernameOrFullName(searchTerm).map { it.toUserResponse() }
    }

    /**
     * Get online users
     */
    fun getOnlineUsers(): List<UserResponse> {
        return userRepository.findByStatus(UserStatus.ONLINE).map { it.toUserResponse() }
    }

    /**
     * Get available online users (not in call)
     */
    fun getAvailableOnlineUsers(): List<UserResponse> {
        return userRepository.findAvailableOnlineUsers().map { it.toUserResponse() }
    }

    /**
     * Get users in call
     */
    fun getUsersInCall(): List<UserResponse> {
        return userRepository.findByIsInCallTrue().map { it.toUserResponse() }
    }

    /**
     * Update user status
     */
    fun updateUserStatus(id: String, status: UserStatus): UserResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("User not found")

        val updatedUser = user.copy(
            status = status,
            lastSeen = if (status == UserStatus.OFFLINE) Instant.now() else user.lastSeen,
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return savedUser.toUserResponse()
    }

    /**
     * Update call status
     */
    fun updateCallStatus(id: String, isInCall: Boolean, callId: String?): UserResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("User not found")

        val updatedUser = user.copy(
            isInCall = isInCall,
            currentCallId = callId,
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return savedUser.toUserResponse()
    }

    /**
     * Verify user email
     */
    fun verifyUserEmail(id: String): UserResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("User not found")

        val updatedUser = user.copy(
            isVerified = true,
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return savedUser.toUserResponse()
    }

    /**
     * Check if username exists
     */
    fun usernameExists(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    /**
     * Check if email exists
     */
    fun emailExists(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    /**
     * Get user statistics
     */
    fun getUserStats(): Map<String, Any> {
        return mapOf(
            "totalUsers" to userRepository.count(),
            "onlineUsers" to userRepository.countByStatus(UserStatus.ONLINE),
            "verifiedUsers" to userRepository.countByIsVerifiedTrue(),
            "usersInCall" to userRepository.countByIsInCallTrue()
        )
    }
}

/**
 * Extension function to convert User to UserResponse
 */
fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id ?: throw IllegalStateException("User ID cannot be null"),
        username = this.username,
        email = this.email,
        fullName = this.fullName,
        bio = this.bio,
        profileImageUrl = this.profileImageUrl,
        phoneNumber = this.phoneNumber,
        isVerified = this.isVerified,
        status = this.status,
        lastSeen = this.lastSeen,
        isInCall = this.isInCall,
        currentCallId = this.currentCallId,
        roles = this.roles,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
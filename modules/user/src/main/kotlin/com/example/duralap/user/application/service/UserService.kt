package com.example.duralap.user.application.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.UserStatus
import com.example.duralap.user.domain.model.User
import com.example.duralap.user.domain.model.toUserResponse
import com.example.duralap.user.domain.repository.UserRepository
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

    fun createUser(request: UserCreateRequest): UserResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

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

    fun getUserById(id: String): UserResponse? {
        return userRepository.findByIdOrNull(id)?.toUserResponse()
    }

    fun getUserByUsername(username: String): UserResponse? {
        return userRepository.findByUsername(username.lowercase()).orElse(null)?.toUserResponse()
    }

    fun getUserByEmail(email: String): UserResponse? {
        return userRepository.findByEmail(email.lowercase()).orElse(null)?.toUserResponse()
    }

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

    fun deleteUser(id: String) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User not found")
        }
        userRepository.deleteById(id)
    }

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toUserResponse() }
    }

    fun searchUsers(searchTerm: String): List<UserResponse> {
        return userRepository.searchByUsernameOrFullName(searchTerm).map { it.toUserResponse() }
    }

    fun getOnlineUsers(): List<UserResponse> {
        return userRepository.findByStatus(UserStatus.ONLINE).map { it.toUserResponse() }
    }

    fun getAvailableOnlineUsers(): List<UserResponse> {
        return userRepository.findAvailableOnlineUsers().map { it.toUserResponse() }
    }

    fun getUsersInCall(): List<UserResponse> {
        return userRepository.findByIsInCallTrue().map { it.toUserResponse() }
    }

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

    fun usernameExists(username: String): Boolean {
        return userRepository.existsByUsername(username.lowercase())
    }

    fun emailExists(email: String): Boolean {
        return userRepository.existsByEmail(email.lowercase())
    }

    fun getUserStats(): Map<String, Any> {
        return mapOf(
            "totalUsers" to userRepository.count(),
            "onlineUsers" to userRepository.countByStatus(UserStatus.ONLINE),
            "verifiedUsers" to userRepository.countByIsVerifiedTrue(),
            "usersInCall" to userRepository.countByIsInCallTrue()
        )
    }
}

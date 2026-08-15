package com.example.duralap.user.application.service

import com.example.duralap.database.dto.UserInfo
import com.example.duralap.database.dto.UserResponse
import com.example.duralap.database.dto.toUserInfo
import com.example.duralap.database.model.UserStatus
import com.example.duralap.user.api.UserModuleApi
import com.example.duralap.user.application.cache.UserCache
import com.example.duralap.user.domain.model.toUserResponse
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserModuleApiImpl(
    private val userRepository: UserRepository,
    private val userCache: UserCache,
    private val userService: UserService
) : UserModuleApi {

    override fun findUserById(id: String): UserResponse? {
        return userCache.getUserResponseById(id)
    }

    override fun findUserByUsername(username: String): UserResponse? {
        return userCache.getUserByUsername(username)?.toUserResponse()
    }

    override fun findUserByEmail(email: String): UserResponse? {
        return userRepository.findByEmail(email.lowercase()).orElse(null)?.toUserResponse()
    }

    override fun findUsersByIds(ids: Set<String>): Map<String, UserResponse> {
        return userCache.getUsersByIds(ids).mapValues { it.value.toUserResponse() }
    }

    override fun findUserInfosByIds(ids: Set<String>): List<UserInfo> {
        val users = userCache.getUsersByIds(ids).values
        return users.map { it.toUserResponse().toUserInfo() }
    }

    override fun existsById(id: String): Boolean {
        return userRepository.existsById(id)
    }

    override fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username.lowercase())
    }

    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email.lowercase())
    }

    override fun updateUserStatus(id: String, status: UserStatus): UserResponse {
        val result = userService.updateUserStatus(id, status)
        userCache.invalidateUserCache(id, result.username)
        return result
    }

    override fun updateCallStatus(id: String, isInCall: Boolean, callId: String?): UserResponse {
        val result = userService.updateCallStatus(id, isInCall, callId)
        userCache.invalidateUserCache(id, result.username)
        return result
    }

    override fun isUserOnline(id: String): Boolean {
        val user = userCache.getUserById(id) ?: return false
        return user.status == UserStatus.ONLINE
    }
}

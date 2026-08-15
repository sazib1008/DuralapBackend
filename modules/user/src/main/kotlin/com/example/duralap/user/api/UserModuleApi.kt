package com.example.duralap.user.api

import com.example.duralap.database.dto.UserInfo
import com.example.duralap.database.dto.UserResponse
import com.example.duralap.database.model.UserStatus

interface UserModuleApi {
    fun findUserById(id: String): UserResponse?
    fun findUserByUsername(username: String): UserResponse?
    fun findUserByEmail(email: String): UserResponse?
    fun findUsersByIds(ids: Set<String>): Map<String, UserResponse>
    fun findUserInfosByIds(ids: Set<String>): List<UserInfo>
    fun existsById(id: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun updateUserStatus(id: String, status: UserStatus): UserResponse
    fun updateCallStatus(id: String, isInCall: Boolean, callId: String?): UserResponse
    fun isUserOnline(id: String): Boolean
}

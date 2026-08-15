package com.example.duralap.auth.api

import com.example.duralap.database.dto.*

interface AuthModuleApi {
    fun register(request: UserCreateRequest): UserResponse
    fun login(request: LoginRequest): AuthResponse
    fun refreshToken(request: TokenRefreshRequest): AuthResponse
    fun logout(token: String?)
    fun getCurrentUserProfile(username: String): UserResponse
}

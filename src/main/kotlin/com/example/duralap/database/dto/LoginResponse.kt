package com.example.duralap.database.dto

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
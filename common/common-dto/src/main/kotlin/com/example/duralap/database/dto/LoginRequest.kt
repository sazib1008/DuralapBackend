package com.example.duralap.database.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "Username or email is required")
    val usernameOrEmail: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)
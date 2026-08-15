package com.example.duralap.auth.web.controller

import com.example.duralap.database.dto.*
import com.example.duralap.auth.application.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: UserCreateRequest): ResponseEntity<UserResponse> {
        val result = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val result = authService.login(request)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: TokenRefreshRequest): ResponseEntity<AuthResponse> {
        val result = authService.refreshToken(request)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String?): ResponseEntity<Any> {
        authService.logout(token)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/profile")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication?.name ?: throw IllegalArgumentException("Not authenticated")
        val result = authService.getCurrentUserProfile(username)
        return ResponseEntity.ok(result)
    }
}

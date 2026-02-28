package com.example.duralap.controller

import com.example.duralap.database.dto.LoginRequest
import com.example.duralap.database.dto.LoginResponse
import com.example.duralap.database.dto.toUserResponse
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.security.JwtTokenProvider
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:3000"]) // Adjust for your frontend
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * User login
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return try {
            // Find user by username or email
            val user = userRepository.findByUsernameOrEmail(request.usernameOrEmail)
                .orElseThrow { IllegalArgumentException("Invalid credentials") }

            // Check password
            if (!passwordEncoder.matches(request.password, user.password)) {
                throw IllegalArgumentException("Invalid credentials")
            }

            // Authenticate user
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    user.username,
                    request.password
                )
            )
            SecurityContextHolder.getContext().authentication = authentication

            // Generate JWT token
            val token = jwtTokenProvider.generateToken(user.username)

            // Update last seen
            val updatedUser = user.copy(lastSeen = java.time.Instant.now())
            userRepository.save(updatedUser)

            val response = LoginResponse(
                token = token,
                user = updatedUser.toUserResponse()
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    fun getCurrentUser(): ResponseEntity<com.example.duralap.database.dto.UserResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name

        val user = userRepository.findByUsername(username)
            .orElseThrow { IllegalArgumentException("User not found") }

        return ResponseEntity.ok(user.toUserResponse())
    }

    /**
     * Refresh token
     */
    @PostMapping("/refresh")
    fun refreshToken(): ResponseEntity<Map<String, String>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name

        val token = jwtTokenProvider.generateToken(username)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}
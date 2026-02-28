package com.example.duralap.controller

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.User
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.security.JwtTokenProvider
import com.example.duralap.service.RefreshTokenService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:3000"])
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val user = userRepository.findByUsernameOrEmail(request.usernameOrEmail)
                .orElseThrow { IllegalArgumentException("Invalid credentials") }

            if (!passwordEncoder.matches(request.password, user.password)) {
                throw IllegalArgumentException("Invalid credentials")
            }

            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    user.username,
                    request.password
                )
            )
            SecurityContextHolder.getContext().authentication = authentication

            val roles = user.roles.map { "ROLE_${it.name}" }.toSet()
            val accessToken = jwtTokenProvider.generateAccessToken(user.username, roles)
            val refreshToken = refreshTokenService.createRefreshToken(user)

            val updatedUser = user.copy(lastSeen = java.time.Instant.now())
            userRepository.save(updatedUser)

            val authResponse = AuthResponse(
                accessToken = accessToken,
                refreshToken = refreshToken.token,
                expiresIn = 86400, // 24 hours in seconds
                user = updatedUser.toUserResponse()
            )

            ResponseEntity.ok(authResponse)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: TokenRefreshRequest): ResponseEntity<AuthResponse> {
        val refreshToken = refreshTokenService.findByToken(request.refreshToken)
            .orElseThrow { RuntimeException("Refresh token not found") }

        refreshTokenService.verifyExpiration(refreshToken)

        val user = userRepository.findById(refreshToken.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val roles = user.roles.map { "ROLE_${it.name}" }.toSet()
        val newAccessToken = jwtTokenProvider.generateAccessToken(user.username, roles)
        val newRefreshToken = refreshTokenService.createRefreshToken(user) // Create new refresh token

        // Optionally revoke the old refresh token
        refreshTokenService.revokeToken(request.refreshToken)

        val updatedUser = user.copy(lastSeen = java.time.Instant.now())
        userRepository.save(updatedUser)

        val authResponse = AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = 86400, // 24 hours in seconds
            user = updatedUser.toUserResponse()
        )

        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String?): ResponseEntity<Any> {
        try {
            val jwt = token?.substring(7) // Remove "Bearer " prefix
            
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                // Extract username from the token
                val username = jwtTokenProvider.getUsernameFromToken(jwt)
                
                // Find user and invalidate their refresh tokens
                val user = userRepository.findByUsername(username)
                    .orElseThrow { IllegalArgumentException("User not found") }
                
                refreshTokenService.revokeAllTokensForUser(user.id!!)
            }
            
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/profile")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication?.name ?: return ResponseEntity.badRequest().build()

        val user = userRepository.findByUsername(username)
            .orElseThrow { IllegalArgumentException("User not found") }

        return ResponseEntity.ok(user.toUserResponse())
    }
}
package com.example.duralap.controller

import com.example.duralap.database.dto.*
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
import java.time.Instant

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
        val user = userRepository.findByUsernameOrEmail(request.usernameOrEmail)
            .orElseThrow { IllegalArgumentException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user.username, request.password)
        )
        SecurityContextHolder.getContext().authentication = authentication

        val roles = user.roles.map { "ROLE_\${it.name}" }.toSet()
        val accessToken = jwtTokenProvider.generateAccessToken(user.username, roles)
        val refreshToken = refreshTokenService.createRefreshToken(user)

        val updatedUser = user.copy(lastSeen = Instant.now())
        userRepository.save(updatedUser)

        val authResponse = AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = 86400, // 24 hours in seconds
            user = updatedUser.toUserResponse()
        )

        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: TokenRefreshRequest): ResponseEntity<AuthResponse> {
        val refreshToken = refreshTokenService.findByToken(request.refreshToken)
            .orElseThrow { IllegalArgumentException("Refresh token not found") }

        refreshTokenService.verifyExpiration(refreshToken)

        val user = userRepository.findById(refreshToken.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val roles = user.roles.map { "ROLE_\${it.name}" }.toSet()
        val newAccessToken = jwtTokenProvider.generateAccessToken(user.username, roles)
        val newRefreshToken = refreshTokenService.createRefreshToken(user)

        refreshTokenService.revokeToken(request.refreshToken)

        val updatedUser = user.copy(lastSeen = Instant.now())
        userRepository.save(updatedUser)

        val authResponse = AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = 86400,
            user = updatedUser.toUserResponse()
        )

        return ResponseEntity.ok(authResponse)
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") token: String?): ResponseEntity<Any> {
        val jwt = token?.removePrefix("Bearer ")
            ?: throw IllegalArgumentException("Missing token")
        
        if (jwtTokenProvider.validateToken(jwt)) {
            val username = jwtTokenProvider.getUsernameFromToken(jwt)
            val user = userRepository.findByUsername(username)
                .orElseThrow { IllegalArgumentException("User not found") }
            
            refreshTokenService.revokeAllTokensForUser(user.id!!)
        } else {
            throw IllegalArgumentException("Invalid token")
        }
        
        return ResponseEntity.ok().build()
    }

    @GetMapping("/profile")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication?.name ?: throw IllegalArgumentException("Not authenticated")

        val user = userRepository.findByUsername(username)
            .orElseThrow { IllegalArgumentException("User not found") }

        return ResponseEntity.ok(user.toUserResponse())
    }
}
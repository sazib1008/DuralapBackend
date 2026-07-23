package com.example.duralap.auth.service

import com.example.duralap.database.dto.*
import com.example.duralap.database.model.*
import com.example.duralap.database.repository.UserRepository
import com.example.duralap.database.repository.UserConversationsRepository
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userConversationsRepository: UserConversationsRepository,
    private val conversationRepository: ConversationRepository
) {

    @Transactional
    fun register(request: UserCreateRequest): UserResponse {
        val secureRequest = request.copy(roles = setOf(Role.USER))
        
        if (userRepository.existsByUsername(secureRequest.username)) {
            throw IllegalArgumentException("Username already exists")
        }
        if (userRepository.existsByEmail(secureRequest.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            id = UUID.randomUUID().toString(),
            username = secureRequest.username.lowercase(),
            email = secureRequest.email.lowercase(),
            password = passwordEncoder.encode(secureRequest.password),
            fullName = secureRequest.fullName,
            bio = secureRequest.bio,
            phoneNumber = secureRequest.phoneNumber,
            roles = secureRequest.roles,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedUser = userRepository.save(user)
        return savedUser.toUserResponse()
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByUsernameOrEmail(request.usernameOrEmail)
            .orElseThrow { IllegalArgumentException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(user.username, request.password)
        )
        SecurityContextHolder.getContext().authentication = authentication

        val roles = user.roles.map { "ROLE_${it.name}" }.toSet()
        val accessToken = jwtTokenProvider.generateAccessToken(user.username, roles)
        val refreshToken = refreshTokenService.createRefreshToken(user)

        val updatedUser = user.copy(lastSeen = Instant.now())
        userRepository.save(updatedUser)

        val userId = updatedUser.id!!
        val conversationIds = userConversationsRepository.findById(userId).orElseGet {
            val existingConvs = conversationRepository.findByParticipantIdsContaining(userId)
            val ids = existingConvs.mapNotNull { it.id }.toSet()
            userConversationsRepository.save(UserConversations(userId, ids))
        }.conversationIds

        val userConversationsDto = UserConversationsDto(userId, conversationIds)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken.token,
            expiresIn = 86400, // 24 hours in seconds
            user = updatedUser.toUserResponse(),
            userConversations = userConversationsDto
        )
    }

    @Transactional
    fun refreshToken(request: TokenRefreshRequest): AuthResponse {
        val refreshToken = refreshTokenService.findByToken(request.refreshToken)
            .orElseThrow { com.example.duralap.exception.TokenRefreshException("Refresh token not found") }

        refreshTokenService.verifyExpiration(refreshToken)

        val user = userRepository.findById(refreshToken.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val roles = user.roles.map { "ROLE_${it.name}" }.toSet()
        val newAccessToken = jwtTokenProvider.generateAccessToken(user.username, roles)
        val newRefreshToken = refreshTokenService.createRefreshToken(user)

        refreshTokenService.revokeToken(request.refreshToken)

        val updatedUser = user.copy(lastSeen = Instant.now())
        userRepository.save(updatedUser)

        val userId = updatedUser.id!!
        val conversationIds = userConversationsRepository.findById(userId).orElseGet {
            val existingConvs = conversationRepository.findByParticipantIdsContaining(userId)
            val ids = existingConvs.mapNotNull { it.id }.toSet()
            userConversationsRepository.save(UserConversations(userId, ids))
        }.conversationIds

        val userConversationsDto = UserConversationsDto(userId, conversationIds)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken.token,
            expiresIn = 86400,
            user = updatedUser.toUserResponse(),
            userConversations = userConversationsDto
        )
    }

    @Transactional
    fun logout(token: String?) {
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
    }

    fun getCurrentUserProfile(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
            .orElseThrow { IllegalArgumentException("User not found") }
        return user.toUserResponse()
    }
}

package com.example.duralap.user.domain.model

import com.example.duralap.database.dto.UserResponse
import com.example.duralap.database.model.Role
import com.example.duralap.database.model.UserStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * MongoDB document for User entity with high-throughput indexing.
 */
@Document("users")
@CompoundIndexes(
    CompoundIndex(name = "status_in_call_idx", def = "{'status': 1, 'isInCall': 1}"),
    CompoundIndex(name = "username_email_idx", def = "{'username': 1, 'email': 1}")
)
data class User(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val username: String,

    @Indexed(unique = true)
    val email: String,

    val password: String? = null,

    val fullName: String? = null,

    val bio: String? = null,

    val profileImageUrl: String? = null,

    val phoneNumber: String? = null,

    val isVerified: Boolean = false,

    @Indexed
    val status: UserStatus = UserStatus.OFFLINE,

    val lastSeen: Instant = Instant.now(),

    val isInCall: Boolean = false,

    val currentCallId: String? = null,

    val roles: Set<Role> = setOf(Role.USER),

    val isOAuth2User: Boolean = false,

    val createdAt: Instant = Instant.now(),

    val updatedAt: Instant = Instant.now()
)

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id ?: throw IllegalStateException("User ID cannot be null"),
        username = this.username,
        email = this.email,
        fullName = this.fullName,
        bio = this.bio,
        profileImageUrl = this.profileImageUrl,
        phoneNumber = this.phoneNumber,
        isVerified = this.isVerified,
        status = this.status,
        lastSeen = this.lastSeen,
        isInCall = this.isInCall,
        currentCallId = this.currentCallId,
        roles = this.roles,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

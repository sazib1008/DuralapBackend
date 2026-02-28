package com.example.duralap.database.dto

import com.example.duralap.database.model.Role
import com.example.duralap.database.model.UserStatus
import java.time.Instant

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String?,
    val bio: String?,
    val profileImageUrl: String?,
    val phoneNumber: String?,
    val isVerified: Boolean,
    val status: UserStatus,
    val lastSeen: Instant?,
    val isInCall: Boolean,
    val currentCallId: String?,
    val roles: Set<Role>,
    val createdAt: Instant,
    val updatedAt: Instant
)

fun UserResponse.toPublicProfile(): PublicUserProfile {
    return PublicUserProfile(
        id = this.id,
        username = this.username,
        fullName = this.fullName,
        bio = this.bio,
        profileImageUrl = this.profileImageUrl,
        status = this.status,
        isVerified = this.isVerified,
        lastSeen = this.lastSeen
    )
}

data class PublicUserProfile(
    val id: String,
    val username: String,
    val fullName: String?,
    val bio: String?,
    val profileImageUrl: String?,
    val status: UserStatus,
    val isVerified: Boolean,
    val lastSeen: Instant?
)
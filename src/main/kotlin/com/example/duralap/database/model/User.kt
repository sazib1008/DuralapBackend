package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "users")
data class User(

    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val username: String,                 // Unique username

    @Indexed(unique = true)
    val email: String,                    // Unique email

    val password: String,                 // Encrypted password (BCrypt)

    val fullName: String? = null,         // Optional full name
    val bio: String? = null,              // Optional bio
    val profileImageUrl: String? = null,  // Profile picture URL
    val phoneNumber: String? = null,      // Optional phone number

    val isVerified: Boolean = false,      // Email verified?

    val status: UserStatus = UserStatus.OFFLINE,  // Current online/offline status
    val lastSeen: Instant? = null,                // Last seen timestamp

    val isInCall: Boolean = false,        // Whether the user is currently in a call
    val currentCallId: String? = null,    // Current active call ID (optional)

    val roles: Set<Role> = setOf(Role.USER),     // Multiple roles allowed

    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)



enum class UserStatus {
    ONLINE,
    OFFLINE,
    AWAY,
    BUSY
}

enum class Role {
    USER,
    ADMIN,
    MODERATOR
}
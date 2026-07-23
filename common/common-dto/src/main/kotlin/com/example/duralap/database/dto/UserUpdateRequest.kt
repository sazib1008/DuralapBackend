package com.example.duralap.database.dto

import com.example.duralap.database.model.Role
import com.example.duralap.database.model.UserStatus
import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    val fullName: String? = null,

    @field:Size(max = 200, message = "Bio must be less than 200 characters")
    val bio: String? = null,

    val profileImageUrl: String? = null,

    val phoneNumber: String? = null,

    val status: UserStatus? = null,

    val isVerified: Boolean? = null,

    val roles: Set<Role>? = null
)
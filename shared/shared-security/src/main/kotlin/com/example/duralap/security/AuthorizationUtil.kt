package com.example.duralap.security

import com.example.duralap.database.model.Role
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthorizationUtil {

    fun getCurrentUserRoles(): Set<Role> {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication?.isAuthenticated == true) {
            authentication.authorities
                .mapNotNull { authority ->
                    val roleName = authority.authority?.removePrefix("ROLE_") ?: ""
                    try {
                        Role.valueOf(roleName)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                .toSet()
        } else {
            emptySet()
        }
    }

    fun hasRole(role: Role): Boolean {
        return getCurrentUserRoles().contains(role)
    }

    fun hasAnyRole(vararg roles: Role): Boolean {
        val userRoles = getCurrentUserRoles()
        return roles.any { userRoles.contains(it) }
    }

    fun hasAdminRole(): Boolean {
        return hasRole(Role.ADMIN)
    }

    fun hasModeratorRole(): Boolean {
        return hasAnyRole(Role.MODERATOR, Role.ADMIN)
    }

    fun getCurrentUsername(): String? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication?.isAuthenticated == true) {
            authentication.name
        } else {
            null
        }
    }
}
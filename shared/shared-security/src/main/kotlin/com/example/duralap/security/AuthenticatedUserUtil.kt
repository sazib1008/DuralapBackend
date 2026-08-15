package com.example.duralap.security

import org.springframework.security.core.context.SecurityContextHolder

/**
 * Utility object to extract authenticated user information from the Spring Security context.
 */
object AuthenticatedUserUtil {

    /**
     * Get the current authenticated user's username
     * 
     * @return Username from security context
     * @throws IllegalArgumentException if not authenticated
     */
    fun getCurrentUsername(): String {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Not authenticated")
        
        return authentication.name
    }

    /**
     * Get the current authenticated user's ID using a lookup function.
     */
    fun getCurrentUserId(findUserId: (String) -> String?): String {
        val username = getCurrentUsername()
        return findUserId(username)
            ?: throw IllegalArgumentException("Current user not found for username: $username")
    }

    /**
     * Check if the current user is authenticated
     */
    fun isAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null && authentication.isAuthenticated
    }
}

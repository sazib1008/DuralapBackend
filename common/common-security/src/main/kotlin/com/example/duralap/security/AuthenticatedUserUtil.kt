package com.example.duralap.security

import com.example.duralap.database.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Utility object to extract authenticated user information
 */
object AuthenticatedUserUtil {
    
    /**
     * Get the current authenticated user's ID
     * 
     * @param userRepository Repository to lookup user by username
     * @return User ID
     * @throws IllegalArgumentException if not authenticated or user not found
     */
    fun getCurrentUserId(userRepository: UserRepository): String {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw IllegalArgumentException("Not authenticated")
        
        val currentUsername = authentication.name
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }
        
        return currentUser.id!!
    }
    
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
     * Check if the current user is authenticated
     */
    fun isAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication != null && authentication.isAuthenticated
    }
}

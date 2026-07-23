package com.example.duralap.database.repository

import com.example.duralap.database.model.User
import com.example.duralap.database.model.UserStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional

@Repository
interface UserRepository : MongoRepository<User, String> {

    /**
     * Find user by username (case-insensitive)
     */
    @Query("{ 'username': { '\$regex': ?0, '\$options': 'i' } }")
    fun findByUsername(username: String): Optional<User>

    /**
     * Find user by email (case-insensitive)
     */
    @Query("{ 'email': { '\$regex': ?0, '\$options': 'i' } }")
    fun findByEmail(email: String): Optional<User>

    /**
     * Check if username exists
     */
    fun existsByUsername(username: String): Boolean

    /**
     * Check if email exists
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Find users by status
     */
    fun findByStatus(status: UserStatus): List<User>

    /**
     * Find online users
     */
    @Query("{ 'status': 'ONLINE', 'isInCall': false }")
    fun findAvailableOnlineUsers(): List<User>

    /**
     * Find users in call
     */
    fun findByIsInCallTrue(): List<User>

    /**
     * Find users by role
     */
    fun findByRolesContaining(role: String): List<User>

    /**
     * Find verified users
     */
    fun findByIsVerifiedTrue(): List<User>

    /**
     * Find users not seen since a specific time
     */
    fun findByLastSeenBefore(lastSeen: Instant): List<User>

    /**
     * Find users by username or email (for login)
     */
    @Query("{ \$or: [ { 'username': { \$regex: ?0, \$options: 'i' } }, { 'email': { \$regex: ?0, \$options: 'i' } } ] }")
    fun findByUsernameOrEmail(usernameOrEmail: String): Optional<User>

    /**
     * Search users by username or full name (for user search functionality)
     */
    @Query("{ \$or: [ { 'username': { \$regex: ?0, \$options: 'i' } }, { 'fullName': { \$regex: ?0, \$options: 'i' } } ] }")
    fun searchByUsernameOrFullName(searchTerm: String): List<User>

    /**
     * Find users created after a specific date
     */
    fun findByCreatedAtAfter(createdAt: Instant): List<User>

    /**
     * Find users updated after a specific date
     */
    fun findByUpdatedAtAfter(updatedAt: Instant): List<User>

    /**
     * Count users by status
     */
    fun countByStatus(status: UserStatus): Long

    /**
     * Count verified users
     */
    fun countByIsVerifiedTrue(): Long

    /**
     * Count users in call
     */
    fun countByIsInCallTrue(): Long
}
package com.example.duralap.user.domain.repository

import com.example.duralap.database.model.UserStatus
import com.example.duralap.user.domain.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : MongoRepository<User, String> {

    fun findByUsername(username: String): Optional<User>

    fun findByEmail(email: String): Optional<User>

    @Query("{'\$or': [{'username': ?0}, {'email': ?0}]}")
    fun findByUsernameOrEmail(usernameOrEmail: String): Optional<User>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByStatus(status: UserStatus): List<User>

    fun findByIsInCallTrue(): List<User>

    @Query("{'status': 'ONLINE', 'isInCall': false}")
    fun findAvailableOnlineUsers(): List<User>

    @Query("{'\$or': [{'username': {'\$regex': ?0, '\$options': 'i'}}, {'fullName': {'\$regex': ?0, '\$options': 'i'}}]}")
    fun searchByUsernameOrFullName(searchTerm: String): List<User>

    fun countByStatus(status: UserStatus): Long

    fun countByIsVerifiedTrue(): Long

    fun countByIsInCallTrue(): Long
}

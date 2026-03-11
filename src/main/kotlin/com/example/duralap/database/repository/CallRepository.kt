package com.example.duralap.database.repository

import com.example.duralap.database.model.Call
import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional

@Repository
interface CallRepository : MongoRepository<Call, String> {

    /**
     * Find active calls for a user (ongoing calls)
     */
    @Query("{ \$or: [ { 'callerId': ?0 }, { 'calleeId': ?0 } ], 'status': { \$in: ['ACTIVE', 'RINGING'] } }")
    fun findActiveCallsForUser(userId: String): List<Call>

    /**
     * Find recent calls for a user
     */
    @Query("{ \$or: [ { 'callerId': ?0 }, { 'calleeId': ?0 } ] }")
    fun findRecentCallsForUser(userId: String): List<Call>

    /**
     * Find calls by conversation ID
     */
    fun findByConversationId(conversationId: String): List<Call>

    /**
     * Find calls by status
     */
    fun findByStatus(status: CallStatus): List<Call>

    /**
     * Find calls by type
     */
    fun findByCallType(callType: CallType): List<Call>

    /**
     * Find missed calls for a user
     */
    @Query("{ 'calleeId': ?0, 'status': 'MISSED' }")
    fun findMissedCallsForUser(userId: String): List<Call>

    /**
     * Find calls between two specific users
     */
    @Query("{ \$or: [ { 'callerId': ?0, 'calleeId': ?1 }, { 'callerId': ?1, 'calleeId': ?0 } ] }")
    fun findCallsBetweenUsers(user1Id: String, user2Id: String): List<Call>

    /**
     * Find ongoing calls (active status)
     */
    @Query("{ 'status': 'ACTIVE' }")
    fun findOngoingCalls(): List<Call>

    /**
     * Find calls initiated by a user
     */
    fun findByCallerId(callerId: String): List<Call>

    /**
     * Find calls received by a user
     */
    fun findByCalleeId(calleeId: String): List<Call>

    /**
     * Find calls updated after a specific time
     */
    fun findByUpdatedAtAfter(updatedAt: Instant): List<Call>

    /**
     * Count calls by status for a user
     */
    @Query("{ \$or: [ { 'callerId': ?0 }, { 'calleeId': ?0 } ], 'status': ?1 }")
    fun countCallsByStatusForUser(userId: String, status: CallStatus): Long

    /**
     * Find last call between two users
     */
    @Query("{ \$or: [ { 'callerId': ?0, 'calleeId': ?1 }, { 'callerId': ?1, 'calleeId': ?0 } ] }")
    fun findLastCallBetweenUsers(user1Id: String, user2Id: String): Optional<Call>

    /**
     * Count calls by call type for a user
     */
    @Query(value = "{ 'callType': ?0, \$or: [ { 'callerId': ?1 }, { 'calleeId': ?1 } ] }", count = true)
    fun countByCallTypeAndUser(callType: CallType, userId: String): Long
}
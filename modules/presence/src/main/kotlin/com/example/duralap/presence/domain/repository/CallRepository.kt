package com.example.duralap.presence.domain.repository

import com.example.duralap.database.model.CallStatus
import com.example.duralap.database.model.CallType
import com.example.duralap.presence.domain.model.Call
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CallRepository : MongoRepository<Call, String> {

    @Query("{'\$or': [{'callerId': ?0}, {'calleeId': ?0}], 'status': 'ACTIVE'}")
    fun findActiveCallsForUser(userId: String): List<Call>

    @Query("{'\$or': [{'callerId': ?0}, {'calleeId': ?0}]}")
    fun findRecentCallsForUser(userId: String): List<Call>

    @Query("{'calleeId': ?0, 'status': 'MISSED'}")
    fun findMissedCallsForUser(userId: String): List<Call>

    @Query("{'\$or': [{'callerId': ?0, 'calleeId': ?1}, {'callerId': ?1, 'calleeId': ?0}]}")
    fun findCallsBetweenUsers(user1Id: String, user2Id: String): List<Call>

    @Query("{'status': 'ACTIVE'}")
    fun findOngoingCalls(): List<Call>

    @Query(value = "{'\$or': [{'callerId': ?0}, {'calleeId': ?0}], 'status': ?1}", count = true)
    fun countCallsByStatusForUser(userId: String, status: CallStatus): Long

    @Query(value = "{'callType': ?0, '\$or': [{'callerId': ?1}, {'calleeId': ?1}]}", count = true)
    fun countByCallTypeAndUser(callType: CallType, userId: String): Long
}

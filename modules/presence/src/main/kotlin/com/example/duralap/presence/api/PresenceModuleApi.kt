package com.example.duralap.presence.api

import com.example.duralap.database.dto.CallResponse
import com.example.duralap.database.dto.UserPresenceResponse

interface PresenceModuleApi {
    fun isUserOnline(userId: String): Boolean
    fun getUserPresence(userId: String): UserPresenceResponse
    fun getUsersPresence(userIds: List<String>): List<UserPresenceResponse>
    fun registerSession(userId: String, sessionId: String, deviceId: String? = null, clientType: String? = "ANDROID")
    fun heartbeatSession(userId: String, sessionId: String, deviceId: String? = null, clientType: String? = "ANDROID")
    fun removeSession(userId: String, sessionId: String)
    fun setUserOnline(userId: String)
    fun setUserOffline(userId: String)
    fun getCallById(callId: String): CallResponse?
    fun countCalls(): Long
}

package com.example.duralap.presence.application.service

import com.example.duralap.database.dto.CallResponse
import com.example.duralap.database.dto.UserPresenceResponse
import com.example.duralap.presence.api.PresenceModuleApi
import com.example.duralap.presence.application.cache.UserPresenceCache
import com.example.duralap.presence.domain.repository.CallRepository
import org.springframework.stereotype.Service

@Service
class PresenceModuleApiImpl(
    private val presenceService: PresenceService,
    private val presenceCache: UserPresenceCache,
    private val callService: CallService,
    private val callRepository: CallRepository
) : PresenceModuleApi {

    override fun isUserOnline(userId: String): Boolean {
        return presenceService.isUserOnline(userId)
    }

    override fun getUserPresence(userId: String): UserPresenceResponse {
        return presenceService.getUserPresence(userId)
    }

    override fun getUsersPresence(userIds: List<String>): List<UserPresenceResponse> {
        return presenceService.getUsersPresence(userIds)
    }

    override fun registerSession(userId: String, sessionId: String, deviceId: String?, clientType: String?) {
        presenceService.registerSession(userId, sessionId, deviceId, clientType)
    }

    override fun heartbeatSession(userId: String, sessionId: String, deviceId: String?, clientType: String?) {
        presenceService.heartbeatSession(userId, sessionId, deviceId, clientType)
    }

    override fun removeSession(userId: String, sessionId: String) {
        presenceService.removeSession(userId, sessionId)
    }

    override fun setUserOnline(userId: String) {
        presenceCache.setUserOnline(userId)
    }

    override fun setUserOffline(userId: String) {
        presenceCache.setUserOffline(userId)
    }

    override fun getCallById(callId: String): CallResponse? {
        return callService.getCallById(callId)
    }

    override fun countCalls(): Long {
        return callRepository.count()
    }
}

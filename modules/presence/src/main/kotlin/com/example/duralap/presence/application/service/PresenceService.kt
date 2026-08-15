package com.example.duralap.presence.application.service

import com.example.duralap.database.dto.UserPresenceResponse
import com.example.duralap.database.model.UserStatus
import com.example.duralap.events.UserPresenceChangedEvent
import com.example.duralap.presence.application.cache.UserPresenceCache
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class PresenceService(
    private val presenceCache: UserPresenceCache,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val logger = LoggerFactory.getLogger(PresenceService::class.java)

    // Local cache of previously known online state to prevent duplicate state transitions across reap cycles
    private val knownOnlineState = ConcurrentHashMap<String, Boolean>()

    /**
     * Registers a new active session for a user.
     * Transitions OFFLINE -> ONLINE when activeSessionCount goes from 0 to 1.
     */
    fun registerSession(
        userId: String,
        sessionId: String,
        deviceId: String? = null,
        clientType: String? = "ANDROID",
        ttlSeconds: Long = UserPresenceCache.DEFAULT_SESSION_TTL_SECONDS
    ) {
        if (userId.isBlank() || sessionId.isBlank()) return

        val activeSessionsBefore = presenceCache.registerSession(userId, sessionId, deviceId, clientType, ttlSeconds)
        knownOnlineState[userId] = true

        if (activeSessionsBefore == 0L) {
            logger.info("User presence state transition: OFFLINE -> ONLINE for userId={} (session={})", userId, sessionId)
            eventPublisher.publishEvent(
                UserPresenceChangedEvent(
                    userId = userId,
                    status = UserStatus.ONLINE,
                    lastSeen = null,
                    sessionCount = 1,
                    timestamp = Instant.now()
                )
            )
        } else {
            logger.debug("Additional session registered for online user: userId={}, sessionId={}, totalSessions={}", userId, sessionId, activeSessionsBefore + 1)
        }
    }

    /**
     * Refreshes the session TTL on heartbeat without emitting events or touching MongoDB.
     */
    fun heartbeatSession(
        userId: String,
        sessionId: String,
        deviceId: String? = null,
        clientType: String? = "ANDROID"
    ) {
        if (userId.isBlank() || sessionId.isBlank()) return

        val refreshed = presenceCache.heartbeatSession(userId, sessionId)
        if (!refreshed) {
            // If the session expired due to network delay, re-register it cleanly
            logger.debug("Heartbeat received for expired or missing session. Re-registering session: userId={}, sessionId={}", userId, sessionId)
            registerSession(userId, sessionId, deviceId, clientType)
        } else {
            logger.debug("Presence heartbeat refreshed for userId={}, sessionId={}", userId, sessionId)
        }
    }

    /**
     * Removes an active session explicitly.
     * Transitions ONLINE -> OFFLINE when remaining active sessions reaches 0.
     */
    fun removeSession(userId: String, sessionId: String) {
        if (userId.isBlank() || sessionId.isBlank()) return

        val remainingActiveSessions = presenceCache.removeSession(userId, sessionId)

        if (remainingActiveSessions == 0L) {
            knownOnlineState.remove(userId)
            val now = Instant.now()
            logger.info("User presence state transition: ONLINE -> OFFLINE for userId={} (lastSession={})", userId, sessionId)
            eventPublisher.publishEvent(
                UserPresenceChangedEvent(
                    userId = userId,
                    status = UserStatus.OFFLINE,
                    lastSeen = now,
                    sessionCount = 0,
                    timestamp = now
                )
            )
        } else {
            logger.debug("Session removed for user, user remains ONLINE with {} active sessions: userId={}", remainingActiveSessions, userId)
        }
    }

    /**
     * Checks if a user is currently online.
     */
    fun isUserOnline(userId: String): Boolean {
        return presenceCache.isUserOnline(userId)
    }

    /**
     * Gets presence state for a single user.
     */
    fun getUserPresence(userId: String): UserPresenceResponse {
        return presenceCache.getUserPresence(userId)
    }

    /**
     * Gets presence states for a batch of users.
     */
    fun getUsersPresence(userIds: List<String>): List<UserPresenceResponse> {
        return presenceCache.getUsersPresence(userIds).values.toList()
    }

    /**
     * Scheduled background reaper running every 30 seconds.
     * Detects dead sessions whose Redis TTL expired and transitions the user to OFFLINE if activeSessionCount == 0.
     */
    @Scheduled(fixedRate = 30000)
    fun reapStaleSessions() {
        try {
            val trackedUsers = presenceCache.getAllTrackedUserIds()
            trackedUsers.forEach { userId ->
                val activeCount = presenceCache.getActiveSessionCount(userId)
                if (activeCount == 0) {
                    val wasKnownOnline = knownOnlineState.remove(userId) ?: true
                    if (wasKnownOnline) {
                        val now = Instant.now()
                        logger.info("Reaper detected session expiry. User transitioned to OFFLINE: userId={}", userId)
                        eventPublisher.publishEvent(
                            UserPresenceChangedEvent(
                                userId = userId,
                                status = UserStatus.OFFLINE,
                                lastSeen = now,
                                sessionCount = 0,
                                timestamp = now
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error during stale presence session reaping", e)
        }
    }
}

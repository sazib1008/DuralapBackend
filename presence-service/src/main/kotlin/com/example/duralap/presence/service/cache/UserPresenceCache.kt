package com.example.duralap.service.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserPresenceCache(
    private val redisTemplate: StringRedisTemplate
) {
    fun isUserOnline(userId: String): Boolean {
        return redisTemplate.hasKey("user:$userId:presence") == true
    }

    fun setUserOnline(userId: String) {
        // Set presence with 2 minutes TTL, requires periodic heartbeat from WS client
        redisTemplate.opsForValue().set("user:$userId:presence", "online", Duration.ofMinutes(2))
    }

    fun setUserOffline(userId: String) {
        redisTemplate.delete("user:$userId:presence")
    }
}

package com.example.duralap.presence.config

import com.example.duralap.presence.application.signaling.WebRtcSignalingSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer

@Configuration
class RedisPubSubConfig {

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        subscriber: WebRtcSignalingSubscriber
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(subscriber, PatternTopic("rtc:signal:user:*"))
        return container
    }
}

package com.example.duralap.analytics.consumers

import com.example.duralap.events.MessageCreatedEvent
import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.database.model.AnalyticsEvent
import com.example.duralap.database.repository.AnalyticsEventRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AnalyticsEventConsumer(
    private val analyticsEventRepository: AnalyticsEventRepository
) {
    private val logger = LoggerFactory.getLogger(AnalyticsEventConsumer::class.java)

    @KafkaListener(topics = ["message-created"], groupId = "analytics-group")
    fun handleMessageCreated(event: MessageCreatedEvent) {
        logger.info("Analytics received message-created: messageId=${event.id}")
        val analyticsEvent = AnalyticsEvent(
            eventType = "message.created",
            userId = event.senderId,
            timestamp = event.timestamp,
            metadata = mapOf(
                "messageId" to event.id,
                "conversationId" to event.conversationId,
                "type" to event.messageType.name
            )
        )
        analyticsEventRepository.save(analyticsEvent)
    }

    @KafkaListener(topics = ["conversation-created"], groupId = "analytics-group")
    fun handleConversationCreated(event: ConversationCreatedEvent) {
        logger.info("Analytics received conversation-created: conversationId=${event.id}")
        val analyticsEvent = AnalyticsEvent(
            eventType = "conversation.created",
            timestamp = event.createdAt,
            metadata = mapOf(
                "conversationId" to event.id,
                "participantsCount" to event.participantIds.size.toString()
            )
        )
        analyticsEventRepository.save(analyticsEvent)
    }
}

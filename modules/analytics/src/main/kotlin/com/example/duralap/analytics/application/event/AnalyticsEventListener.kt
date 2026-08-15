package com.example.duralap.analytics.application.event

import com.example.duralap.analytics.domain.model.AnalyticsEvent
import com.example.duralap.analytics.domain.repository.AnalyticsEventRepository
import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.events.MessageCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AnalyticsEventListener(
    private val analyticsEventRepository: AnalyticsEventRepository
) {

    private val logger = LoggerFactory.getLogger(AnalyticsEventListener::class.java)

    @EventListener
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

    @EventListener
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

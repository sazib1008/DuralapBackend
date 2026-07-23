package com.example.duralap.notification.consumers

import com.example.duralap.events.MessageCreatedEvent
import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.notification.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NotificationEventConsumer(
    private val notificationService: NotificationService,
    private val conversationRepository: ConversationRepository
) {
    private val logger = LoggerFactory.getLogger(NotificationEventConsumer::class.java)

    @KafkaListener(topics = ["message-created"], groupId = "notification-group")
    fun handleMessageCreated(event: MessageCreatedEvent) {
        logger.info("Received message-created event: messageId=${event.id}, conversationId=${event.conversationId}")
        
        val conversation = conversationRepository.findById(event.conversationId).orElse(null)
        if (conversation == null) {
            logger.warn("Conversation with ID ${event.conversationId} not found for notification routing")
            return
        }
        
        val otherParticipantIds = conversation.participantIds.filter { it != event.senderId }
        for (recipientId in otherParticipantIds) {
            notificationService.createNotification(
                userId = recipientId,
                type = "NEW_MESSAGE",
                title = "New Message",
                body = event.content,
                data = mapOf(
                    "conversationId" to event.conversationId,
                    "messageId" to event.id,
                    "senderId" to event.senderId
                )
            )
        }
    }

    @KafkaListener(topics = ["conversation-created"], groupId = "notification-group")
    fun handleConversationCreated(event: ConversationCreatedEvent) {
        logger.info("Received conversation-created event: conversationId=${event.id}")
        
        for (participantId in event.participantIds) {
            notificationService.createNotification(
                userId = participantId,
                type = "SYSTEM",
                title = "Conversation Started",
                body = "A new conversation has been established.",
                data = mapOf("conversationId" to event.id)
            )
        }
    }
}

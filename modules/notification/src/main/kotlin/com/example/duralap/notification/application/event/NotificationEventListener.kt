package com.example.duralap.notification.application.event

import com.example.duralap.chat.api.ChatModuleApi
import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.events.MessageCreatedEvent
import com.example.duralap.notification.application.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
    private val notificationService: NotificationService,
    private val chatModuleApi: ChatModuleApi
) {

    private val logger = LoggerFactory.getLogger(NotificationEventListener::class.java)

    @EventListener
    fun handleMessageCreated(event: MessageCreatedEvent) {
        logger.info("Notification received message-created: messageId=${event.id}, conversationId=${event.conversationId}")

        val participantIds = chatModuleApi.getParticipantIds(event.conversationId)
        val otherParticipantIds = participantIds.filter { it != event.senderId }

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

    @EventListener
    fun handleConversationCreated(event: ConversationCreatedEvent) {
        logger.info("Notification received conversation-created: conversationId=${event.id}")

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

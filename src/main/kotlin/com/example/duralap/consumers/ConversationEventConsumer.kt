package com.example.duralap.consumers

import com.example.duralap.events.ConversationCreatedEvent
import com.example.duralap.service.ConversationService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class ConversationEventConsumer(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val conversationService: ConversationService
) {

    private val logger = LoggerFactory.getLogger(ConversationEventConsumer::class.java)

    /**
     * Consumes conversation creation events and notifies participants via WebSockets.
     */
    @KafkaListener(topics = ["chat.events.conversation.created"], groupId = "duralap-conversation-workers")
    fun consumeConversationCreatedEvent(event: ConversationCreatedEvent) {
        logger.info("Consumed ConversationCreatedEvent id=${event.id}")

        // Notify each participant individually so they can update their conversation list
        // Clients should subscribe to /user/queue/conversations
        event.participantIds.forEach { userId ->
            conversationService.updateUserConversations(userId, event.id)
            
            val destination = "/user/$userId/queue/conversations"
            simpMessagingTemplate.convertAndSend(destination, event)
            logger.info("Dispatched conversation event to $destination")
        }
    }
}

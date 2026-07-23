package com.example.duralap.consumers

import com.example.duralap.database.model.Message
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.events.MessageCreatedEvent
import org.springframework.data.repository.findByIdOrNull
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class MessageEventConsumer(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    private val logger = LoggerFactory.getLogger(MessageEventConsumer::class.java)

    /**
     * Consumes events and routes to WebSockets for real-time delivery.
     * Persistence is now handled synchronously in the service layer for storage guarantee.
     */
    @KafkaListener(topics = ["chat.events.message.created"], groupId = "duralap-message-workers")
    fun consumeMessageEvent(event: MessageCreatedEvent) {
        logger.info("Consumed MessageCreatedEvent tx_id=${event.id} for delivery")

        // 1. Fetch the message from DB (since it's saved synchronously in Service)
        // This ensures downstream consumers work with the actual persisted state
        val message = messageRepository.findByIdOrNull(event.id) ?: run {
            logger.error("Message ${event.id} not found in MongoDB during event consumption")
            return
        }

        // 2. Broadcast to specific conversation topic (for users currently on the chat screen)
        // Clients sub to /topic/conversation/{conversationId}
        val chatDestination = "/topic/conversation/${event.conversationId}"
        simpMessagingTemplate.convertAndSend(chatDestination, message)
        
        // 3. Reach the other user directly (for notifications even if they aren't on the chat screen)
        // Clients sub to /user/queue/messages
        val conversation = conversationRepository.findByIdOrNull(event.conversationId)
        conversation?.participantIds?.forEach { participantId ->
            // Update the message state for participants
            if (participantId != event.senderId) {
                val userMessageDestination = "/user/$participantId/queue/messages"
                simpMessagingTemplate.convertAndSend(userMessageDestination, message)
                logger.info("Dispatched message delivery to user: $participantId at $userMessageDestination")
            }

            // 4. Update the Dashboard/Conversation List for ALL participants (including sender to sync multi-device)
            val conversationUpdateDestination = "/user/$participantId/queue/conversations"
            val updateEvent = com.example.duralap.events.ConversationUpdatedEvent(
                conversationId = event.conversationId,
                lastMessageContent = event.content,
                lastMessageAt = event.timestamp,
                participantIds = conversation.participantIds
            )
            simpMessagingTemplate.convertAndSend(conversationUpdateDestination, updateEvent)
            logger.info("Dispatched dashboard update to user: $participantId at $conversationUpdateDestination")
        }
        
        logger.info("Successfully dispatched message id=${event.id} to chat $chatDestination")
    }
}

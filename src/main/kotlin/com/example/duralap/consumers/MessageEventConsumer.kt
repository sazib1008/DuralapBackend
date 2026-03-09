package com.example.duralap.consumers

import com.example.duralap.database.model.Message
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.events.MessageCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class MessageEventConsumer(
    private val messageRepository: MessageRepository,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    private val logger = LoggerFactory.getLogger(MessageEventConsumer::class.java)

    /**
     * Consumes events, persists idempotently, and routes to WebSockets.
     */
    @KafkaListener(topics = ["chat.events.message.created"], groupId = "duralap-message-workers")
    fun consumeMessageEvent(event: MessageCreatedEvent) {
        logger.info("Consumed MessageCreatedEvent tx_id=\${event.id}")

        // 1. Idempotency Check: Don't persist if we already have it
        if (messageRepository.existsById(event.id)) {
            logger.warn("Duplicate message processing aborted for tx_id=\${event.id}")
            return
        }

        // 2. Map Event to persistence Domain Model
        val message = Message(
            id = event.id,
            conversationId = event.conversationId,
            senderId = event.senderId,
            content = event.content,
            messageType = event.messageType,
            mediaUrl = event.mediaUrl,
            mediaType = event.mediaType,
            fileName = event.fileName,
            fileSize = event.fileSize,
            isRead = false,
            createdAt = event.timestamp,
            updatedAt = event.timestamp
        )

        // 3. Persist 
        messageRepository.save(message)

        // 4. WebSocket Broadcast to specific conversation topic
        // Clients should sub to /topic/conversation/{conversationId}
        val destination = "/topic/conversation/\${event.conversationId}"
        simpMessagingTemplate.convertAndSend(destination, message)
        
        logger.info("Successfully persisted and dispatched message id=\${event.id} to \$destination")
    }
}

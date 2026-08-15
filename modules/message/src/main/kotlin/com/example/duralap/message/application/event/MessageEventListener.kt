package com.example.duralap.message.application.event

import com.example.duralap.chat.api.ChatModuleApi
import com.example.duralap.events.ConversationUpdatedEvent
import com.example.duralap.events.MessageCreatedEvent
import com.example.duralap.events.MessageStatusUpdatedEvent
import com.example.duralap.message.domain.repository.MessageRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class MessageEventListener(
    private val messageRepository: MessageRepository,
    private val chatModuleApi: ChatModuleApi,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val redisTemplate: StringRedisTemplate
) {

    private val logger = LoggerFactory.getLogger(MessageEventListener::class.java)

    @EventListener
    fun handleMessageCreated(event: MessageCreatedEvent) {
        val message = messageRepository.findByIdOrNull(event.id) ?: run {
            logger.error("Message ${event.id} not found in MongoDB during event consumption")
            return
        }

        val participantIds = chatModuleApi.getParticipantIds(event.conversationId)

        val chatDestination = "/topic/conversation/${event.conversationId}"
        simpMessagingTemplate.convertAndSend(chatDestination, message)

        participantIds.forEach { participantId ->
            val userMessageDestination = "/user/$participantId/queue/messages"
            simpMessagingTemplate.convertAndSend(userMessageDestination, message)

            val conversationUpdateDestination = "/user/$participantId/queue/conversations"
            val updateEvent = ConversationUpdatedEvent(
                conversationId = event.conversationId,
                lastMessageId = event.id,
                lastMessageSenderId = event.senderId,
                lastMessageContent = event.content,
                lastMessageType = event.messageType,
                lastMessageAt = event.timestamp,
                participantIds = participantIds
            )
            simpMessagingTemplate.convertAndSend(conversationUpdateDestination, updateEvent)
            logger.info("Delivered WS message and conversation update for conv=${event.conversationId} to participant: $participantId")
        }
    }

    @EventListener
    fun handleStatusUpdated(event: MessageStatusUpdatedEvent) {
        val chatDestination = "/topic/conversation/${event.conversationId}"
        simpMessagingTemplate.convertAndSend(chatDestination, event)

        val participantIds = chatModuleApi.getParticipantIds(event.conversationId)
        participantIds.forEach { participantId ->
            val userStatusDestination = "/user/$participantId/queue/message-status"
            simpMessagingTemplate.convertAndSend(userStatusDestination, event)
        }
    }
}

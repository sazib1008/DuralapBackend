package com.example.duralap.message.service

import com.example.duralap.chat.api.ChatModuleApi
import com.example.duralap.config.RateLimitingService
import com.example.duralap.database.dto.MessageCreateRequest
import com.example.duralap.database.model.MessageStatus
import com.example.duralap.message.application.cache.ConversationValidationCache
import com.example.duralap.message.application.service.MessageService
import com.example.duralap.message.domain.model.Message
import com.example.duralap.message.domain.repository.MessageRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant

class MessageServiceTest {

    private lateinit var eventPublisher: ApplicationEventPublisher
    private lateinit var conversationValidator: ConversationValidationCache
    private lateinit var messageRepository: MessageRepository
    private lateinit var chatModuleApi: ChatModuleApi
    private lateinit var rateLimitingService: RateLimitingService
    private lateinit var messageService: MessageService

    @BeforeEach
    fun setUp() {
        eventPublisher = mock(ApplicationEventPublisher::class.java)
        conversationValidator = mock(ConversationValidationCache::class.java)
        messageRepository = mock(MessageRepository::class.java)
        chatModuleApi = mock(ChatModuleApi::class.java)
        rateLimitingService = mock(RateLimitingService::class.java)

        messageService = MessageService(
            eventPublisher,
            conversationValidator,
            messageRepository,
            chatModuleApi,
            rateLimitingService
        )
    }

    @Test
    fun `sendMessage should return existing message when clientMsgId is duplicate`() {
        val clientUuid = "uuid-1234"
        val request = MessageCreateRequest(
            conversationId = "conv-1",
            senderId = "user-1",
            content = "Hello duplicate",
            clientMsgId = clientUuid
        )

        val existingMessage = Message(
            id = "msg-99",
            clientMsgId = clientUuid,
            conversationId = "conv-1",
            senderId = "user-1",
            content = "Hello duplicate",
            status = MessageStatus.SENT
        )

        `when`(rateLimitingService.canSendMessage("user-1")).thenReturn(true)
        `when`(messageRepository.findByClientMsgId(clientUuid)).thenReturn(existingMessage)

        val result = messageService.sendMessage(request)

        assertEquals("msg-99", result.id)
        assertEquals(clientUuid, result.clientMsgId)
        verify(messageRepository, never()).save(any(Message::class.java))
    }

    @Test
    fun `sendMessage should persist message and assign SENT status`() {
        val clientUuid = "uuid-5678"
        val request = MessageCreateRequest(
            conversationId = "conv-1",
            senderId = "user-1",
            content = "New message",
            clientMsgId = clientUuid
        )

        `when`(rateLimitingService.canSendMessage("user-1")).thenReturn(true)
        `when`(messageRepository.findByClientMsgId(clientUuid)).thenReturn(null)
        `when`(chatModuleApi.isConversationAccepted("conv-1")).thenReturn(true)
        `when`(messageRepository.save(any(Message::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0) as Message
        }

        val result = messageService.sendMessage(request)

        assertNotNull(result.id)
        assertEquals(clientUuid, result.clientMsgId)
        assertEquals(MessageStatus.SENT, result.status)
        assertEquals("New message", result.content)
    }
}

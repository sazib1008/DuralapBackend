package com.example.duralap.message.web.controller

import com.example.duralap.database.dto.MessageStatusUpdateRequest
import com.example.duralap.database.model.MessageStatus
import com.example.duralap.message.application.service.MessageService
import com.example.duralap.user.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller

@Controller
class MessageAckController(
    private val messageService: MessageService,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(MessageAckController::class.java)

    @MessageMapping("/chat.ack.delivery")
    fun handleDeliveryAck(@Payload request: MessageStatusUpdateRequest, authentication: Authentication?) {
        val username = authentication?.name ?: return
        val user = userRepository.findByUsername(username).orElse(null) ?: return

        logger.info("Received WebSocket delivery ACK for msgId=${request.messageId} from user=${user.id}")
        messageService.updateMessageStatus(request.messageId, MessageStatus.DELIVERED, user.id!!)
    }

    @MessageMapping("/chat.ack.read")
    fun handleReadAck(@Payload request: MessageStatusUpdateRequest, authentication: Authentication?) {
        val username = authentication?.name ?: return
        val user = userRepository.findByUsername(username).orElse(null) ?: return

        logger.info("Received WebSocket read ACK for msgId=${request.messageId} from user=${user.id}")
        messageService.updateMessageStatus(request.messageId, MessageStatus.READ, user.id!!)
    }
}

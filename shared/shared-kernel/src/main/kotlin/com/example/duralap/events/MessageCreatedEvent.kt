package com.example.duralap.events

import com.example.duralap.database.model.MessageStatus
import com.example.duralap.database.model.MessageType
import java.time.Instant

data class MessageCreatedEvent(
    val id: String,
    val clientMsgId: String? = null,
    val conversationId: String,
    val senderId: String,
    val content: String,
    val messageType: MessageType,
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp: Instant
)


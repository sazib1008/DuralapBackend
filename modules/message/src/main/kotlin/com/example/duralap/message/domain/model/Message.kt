package com.example.duralap.message.domain.model

import com.example.duralap.database.dto.MessageResponse
import com.example.duralap.database.model.MessageStatus
import com.example.duralap.database.model.MessageType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("messages")
@CompoundIndexes(
    CompoundIndex(name = "conversation_created_idx", def = "{'conversationId': 1, 'createdAt': -1}"),
    CompoundIndex(name = "unread_messages_idx", def = "{'conversationId': 1, 'senderId': 1, 'isRead': 1}")
)
data class Message(
    @Id
    val id: String? = null,

    val clientMsgId: String? = null,

    @Indexed
    val conversationId: String,

    @Indexed
    val senderId: String,

    val content: String,

    val messageType: MessageType = MessageType.TEXT,

    val mediaUrl: String? = null,

    val mediaType: String? = null,

    val fileName: String? = null,

    val fileSize: Long? = null,

    val status: MessageStatus = MessageStatus.SENT,

    val isRead: Boolean = false,

    val readAt: Instant? = null,

    val createdAt: Instant = Instant.now(),

    val updatedAt: Instant = Instant.now()
)

fun Message.toMessageResponse(): MessageResponse {
    return MessageResponse(
        id = this.id ?: throw IllegalStateException("Message ID cannot be null"),
        conversationId = this.conversationId,
        senderId = this.senderId,
        content = this.content,
        messageType = this.messageType,
        mediaUrl = this.mediaUrl,
        mediaType = this.mediaType,
        fileName = this.fileName,
        fileSize = this.fileSize,
        isRead = this.isRead,
        readAt = this.readAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        senderInfo = null,
        clientMsgId = this.clientMsgId,
        status = this.status
    )
}

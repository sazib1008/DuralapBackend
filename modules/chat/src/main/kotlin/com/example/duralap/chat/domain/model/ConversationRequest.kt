package com.example.duralap.chat.domain.model

import com.example.duralap.database.dto.ConversationRequestResponse
import com.example.duralap.database.model.ConversationStatus
import com.example.duralap.user.domain.model.User
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("conversation_requests")
@CompoundIndex(name = "idx_sender_recipient_status", def = "{'senderId': 1, 'recipientId': 1, 'status': 1}")
data class ConversationRequest(
    @Id
    val id: String? = null,

    @Indexed
    val senderId: String,

    @Indexed
    val recipientId: String,

    val conversationId: String,

    val status: ConversationStatus = ConversationStatus.PENDING,

    val initialMessage: String? = null,

    val requestedAt: Instant = Instant.now(),

    val respondedAt: Instant? = null,

    val respondedBy: String? = null
)

fun ConversationRequest.toConversationRequestResponse(sender: User?): ConversationRequestResponse {
    return ConversationRequestResponse(
        id = this.id ?: throw IllegalStateException("Request ID cannot be null"),
        senderId = this.senderId,
        senderUsername = sender?.username,
        senderFullName = sender?.fullName,
        senderProfileImageUrl = sender?.profileImageUrl,
        recipientId = this.recipientId,
        conversationId = this.conversationId,
        status = this.status,
        initialMessage = this.initialMessage,
        requestedAt = this.requestedAt,
        respondedAt = this.respondedAt
    )
}

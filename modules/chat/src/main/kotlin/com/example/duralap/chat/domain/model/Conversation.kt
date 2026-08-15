package com.example.duralap.chat.domain.model

import com.example.duralap.database.dto.ConversationResponse
import com.example.duralap.database.dto.MessageResponse
import com.example.duralap.database.dto.UserInfo
import com.example.duralap.database.model.ConversationStatus
import com.example.duralap.database.model.MessageType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("conversations")
@CompoundIndexes(
    CompoundIndex(name = "participants_idx", def = "{'participantIds': 1}"),
    CompoundIndex(name = "participants_last_msg_idx", def = "{'participantIds': 1, 'lastMessageAt': -1}"),
    CompoundIndex(name = "participants_status_last_msg_idx", def = "{'participantIds': 1, 'status': 1, 'lastMessageAt': -1}")
)
data class Conversation(
    @Id
    val id: String? = null,

    @Indexed
    val participantIds: Set<String>,

    val status: ConversationStatus = ConversationStatus.ACCEPTED,

    val createdAt: Instant = Instant.now(),

    var lastMessageId: String? = null,
    var lastMessageSenderId: String? = null,
    var lastMessageContent: String? = null,
    var lastMessageType: MessageType? = null,
    var lastMessageAt: Instant? = null
)

fun Conversation.toConversationResponse(
    lastMessage: MessageResponse? = null,
    unreadCount: Int = 0,
    participants: List<UserInfo>? = null
): ConversationResponse {
    return ConversationResponse(
        id = this.id ?: throw IllegalStateException("Conversation ID cannot be null"),
        participantIds = this.participantIds,
        status = this.status,
        createdAt = this.createdAt,
        lastMessage = lastMessage,
        unreadCount = unreadCount,
        participants = participants
    )
}

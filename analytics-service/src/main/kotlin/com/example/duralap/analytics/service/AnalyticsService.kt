package com.example.duralap.analytics.service

import com.example.duralap.database.repository.UserRepository
import com.example.duralap.database.repository.MessageRepository
import com.example.duralap.database.repository.ConversationRepository
import com.example.duralap.database.repository.CallRepository
import com.example.duralap.database.repository.AnalyticsEventRepository
import org.springframework.stereotype.Service

@Service
class AnalyticsService(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository,
    private val callRepository: CallRepository,
    private val analyticsEventRepository: AnalyticsEventRepository
) {

    fun getMetricsSummary(): Map<String, Any> {
        val totalUsers = userRepository.count()
        val totalMessages = messageRepository.count()
        val totalConversations = conversationRepository.count()
        val totalCalls = callRepository.count()
        val totalAnalyticsEvents = analyticsEventRepository.count()

        return mapOf(
            "totalUsers" to totalUsers,
            "totalMessages" to totalMessages,
            "totalConversations" to totalConversations,
            "totalCalls" to totalCalls,
            "totalAnalyticsEvents" to totalAnalyticsEvents,
            "messageCreatedEvents" to analyticsEventRepository.countByEventType("message.created"),
            "conversationCreatedEvents" to analyticsEventRepository.countByEventType("conversation.created")
        )
    }
}

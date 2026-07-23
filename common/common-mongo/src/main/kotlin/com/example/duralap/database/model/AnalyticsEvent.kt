package com.example.duralap.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "analytics_events")
data class AnalyticsEvent(
    @Id
    val id: String? = null,
    val eventType: String,            // message.created, conversation.created, call.initiated, user.login, etc.
    val userId: String? = null,
    val timestamp: Instant = Instant.now(),
    val metadata: Map<String, String> = emptyMap()
)

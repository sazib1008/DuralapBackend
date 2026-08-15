package com.example.duralap.analytics.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("analytics_events")
@CompoundIndex(name = "event_type_timestamp_idx", def = "{'eventType': 1, 'timestamp': -1}")
data class AnalyticsEvent(
    @Id
    val id: String? = null,

    @Indexed
    val eventType: String,

    val userId: String? = null,

    val timestamp: Instant = Instant.now(),

    val metadata: Map<String, Any> = emptyMap()
)

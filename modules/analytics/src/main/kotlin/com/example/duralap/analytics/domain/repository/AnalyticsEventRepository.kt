package com.example.duralap.analytics.domain.repository

import com.example.duralap.analytics.domain.model.AnalyticsEvent
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyticsEventRepository : MongoRepository<AnalyticsEvent, String> {
    fun countByEventType(eventType: String): Long
}

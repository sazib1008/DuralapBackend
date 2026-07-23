package com.example.duralap.database.repository

import com.example.duralap.database.model.AnalyticsEvent
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyticsEventRepository : MongoRepository<AnalyticsEvent, String> {
    fun countByEventType(eventType: String): Long
    fun findByEventType(eventType: String): List<AnalyticsEvent>
}

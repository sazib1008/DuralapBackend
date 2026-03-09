package com.example.duralap.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.beans.factory.annotation.Value

@Configuration
class MongoIndexConfig(private val mongoTemplate: MongoTemplate) {

    @Value("\${spring.data.mongodb.uri:NOT_FOUND}")
    private lateinit var mongoUri: String

    private val logger = LoggerFactory.getLogger(MongoIndexConfig::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun ensureIndexes() {
        logger.info("Initializing MongoDB Compound Indexes for high-throughput scaling...")
        logger.info("Current injected MongoDB URI is: \$mongoUri")

        ensureMessageIndexes()
        ensureCallIndexes()
        ensureUserIndexes()
        
        logger.info("MongoDB Indexes synchronized successfully.")
    }

    private fun ensureMessageIndexes() {
        val messageCollection = "messages"

        // 1. Conversation sorted timeline (CQRS Query Side fast lookup)
        // db.messages.createIndex({ conversationId: 1, createdAt: -1 })
        mongoTemplate.indexOps(messageCollection).ensureIndex(
            Index().on("conversationId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC)
        )

        // 2. Unread messages optimization
        // db.messages.createIndex({ conversationId: 1, senderId: 1, isRead: 1 })
        mongoTemplate.indexOps(messageCollection).ensureIndex(
            Index().on("conversationId", Sort.Direction.ASC)
                   .on("senderId", Sort.Direction.ASC)
                   .on("isRead", Sort.Direction.ASC)
        )
    }

    private fun ensureCallIndexes() {
        val callCollection = "calls"

        // 1. Fast active calls resolution per user without collection scan
        // db.calls.createIndex({ calleeId: 1, status: 1 })
        mongoTemplate.indexOps(callCollection).ensureIndex(
            Index().on("calleeId", Sort.Direction.ASC).on("status", Sort.Direction.ASC)
        )
        
        mongoTemplate.indexOps(callCollection).ensureIndex(
            Index().on("callerId", Sort.Direction.ASC).on("status", Sort.Direction.ASC)
        )

        // 2. Call history sorting between specific users
        // db.calls.createIndex({ callerId: 1, calleeId: 1, createdAt: -1 })
        mongoTemplate.indexOps(callCollection).ensureIndex(
            Index().on("callerId", Sort.Direction.ASC)
                   .on("calleeId", Sort.Direction.ASC)
                   .on("createdAt", Sort.Direction.DESC)
        )
    }

    private fun ensureUserIndexes() {
        val userCollection = "users"

        // Optimize queries fetching currently available/active users
        // db.users.createIndex({ status: 1, isInCall: 1 })
        mongoTemplate.indexOps(userCollection).ensureIndex(
            Index().on("status", Sort.Direction.ASC).on("isInCall", Sort.Direction.ASC)
        )
    }
}

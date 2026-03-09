package com.example.duralap.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoConfig {

    private val logger = LoggerFactory.getLogger(MongoConfig::class.java)

    @Value("\${spring.data.mongodb.uri}")
    private lateinit var mongoUri: String

    @Bean
    fun mongoClient(): MongoClient {
        logger.info("Initializing explicit MongoClient with URI: {}", mongoUri)
        return MongoClients.create(mongoUri)
    }
}

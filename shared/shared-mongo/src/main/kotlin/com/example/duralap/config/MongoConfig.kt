package com.example.duralap.config

import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.example.duralap"])
class MongoConfig {

    private val logger = LoggerFactory.getLogger(MongoConfig::class.java)

    @Value("\${spring.data.mongodb.uri}")
    private lateinit var mongoUri: String

    @Bean
    fun mongoClient(): MongoClient {
        logger.info("Initializing explicit MongoClient from configured MongoDB connection string.")
        return MongoClients.create(mongoUri)
    }

    @Bean
    fun mongoDatabaseFactory(mongoClient: MongoClient): MongoDatabaseFactory {
        val connectionString = ConnectionString(mongoUri)
        val databaseName = connectionString.database ?: "duralap"
        logger.info("Initializing SimpleMongoClientDatabaseFactory with database name: {}", databaseName)
        return SimpleMongoClientDatabaseFactory(mongoClient, databaseName)
    }
}

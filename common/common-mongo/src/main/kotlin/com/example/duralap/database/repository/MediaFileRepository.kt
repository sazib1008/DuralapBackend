package com.example.duralap.database.repository

import com.example.duralap.database.model.MediaFile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MediaFileRepository : MongoRepository<MediaFile, String> {
    fun findByOwnerId(ownerId: String): List<MediaFile>
    fun findByConversationId(conversationId: String): List<MediaFile>
    fun findByMessageId(messageId: String): Optional<MediaFile>
}

package com.example.duralap.media.domain.repository

import com.example.duralap.media.domain.model.MediaFile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MediaFileRepository : MongoRepository<MediaFile, String> {
    fun findByOwnerId(ownerId: String): List<MediaFile>
}

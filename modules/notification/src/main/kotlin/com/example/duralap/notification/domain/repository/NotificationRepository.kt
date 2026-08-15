package com.example.duralap.notification.domain.repository

import com.example.duralap.notification.domain.model.Notification
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : MongoRepository<Notification, String> {

    fun findByUserId(userId: String, pageable: Pageable): List<Notification>

    fun findByUserIdAndStatus(userId: String, status: String, pageable: Pageable): List<Notification>

    fun countByUserIdAndStatus(userId: String, status: String): Long
}

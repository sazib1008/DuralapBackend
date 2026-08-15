package com.example.duralap.notification.application.service

import com.example.duralap.notification.domain.model.Notification
import com.example.duralap.notification.domain.repository.NotificationRepository
import com.example.duralap.user.api.UserModuleApi
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val userModuleApi: UserModuleApi,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun createNotification(
        userId: String,
        type: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Notification {
        val notification = Notification(
            userId = userId,
            type = type,
            title = title,
            body = body,
            data = data,
            status = "CREATED",
            createdAt = Instant.now()
        )
        val saved = notificationRepository.save(notification)

        val user = userModuleApi.findUserById(userId)
        if (user != null) {
            messagingTemplate.convertAndSendToUser(
                user.username,
                "/queue/notifications",
                saved
            )
        }

        return saved
    }

    fun getNotificationsForUser(userId: String, page: Int, size: Int): List<Notification> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return notificationRepository.findByUserId(userId, pageable)
    }

    fun markNotificationsAsRead(userId: String, notificationIds: List<String>?) {
        if (notificationIds.isNullOrEmpty()) {
            val pageable = PageRequest.of(0, 1000)
            val unread = notificationRepository.findByUserIdAndStatus(userId, "CREATED", pageable)
            val readNotifications = unread.map { 
                it.copy(status = "READ", readAt = Instant.now()) 
            }
            notificationRepository.saveAll(readNotifications)
        } else {
            val notifications = notificationRepository.findAllById(notificationIds)
            val updated = notifications
                .filter { it.userId == userId && it.status == "CREATED" }
                .map { it.copy(status = "READ", readAt = Instant.now()) }
            notificationRepository.saveAll(updated)
        }
    }
}

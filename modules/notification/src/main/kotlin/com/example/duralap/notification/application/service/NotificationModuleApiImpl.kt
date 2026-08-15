package com.example.duralap.notification.application.service

import com.example.duralap.notification.api.NotificationModuleApi
import com.example.duralap.notification.domain.model.Notification
import org.springframework.stereotype.Service

@Service
class NotificationModuleApiImpl(
    private val notificationService: NotificationService
) : NotificationModuleApi {

    override fun createNotification(
        userId: String,
        type: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): Notification {
        return notificationService.createNotification(userId, type, title, body, data)
    }

    override fun getNotificationsForUser(userId: String, page: Int, size: Int): List<Notification> {
        return notificationService.getNotificationsForUser(userId, page, size)
    }
}

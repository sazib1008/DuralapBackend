package com.example.duralap.notification.api

import com.example.duralap.notification.domain.model.Notification

interface NotificationModuleApi {
    fun createNotification(
        userId: String,
        type: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Notification
    fun getNotificationsForUser(userId: String, page: Int, size: Int): List<Notification>
}

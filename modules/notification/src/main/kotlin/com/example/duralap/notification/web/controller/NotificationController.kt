package com.example.duralap.notification.web.controller

import com.example.duralap.notification.domain.model.Notification
import com.example.duralap.notification.application.service.NotificationService
import com.example.duralap.security.AuthenticatedUserUtil
import com.example.duralap.user.domain.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = ["*"])
class NotificationController(
    private val notificationService: NotificationService,
    private val userRepository: UserRepository
) {

    @GetMapping
    fun getNotifications(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<Notification>> {
        val currentUsername = AuthenticatedUserUtil.getCurrentUsername()
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        val notifications = notificationService.getNotificationsForUser(currentUser.id!!, page, size)
        return ResponseEntity.ok(notifications)
    }

    @PatchMapping("/read")
    fun markAsRead(
        @RequestBody(required = false) notificationIds: List<String>?
    ): ResponseEntity<Unit> {
        val currentUsername = AuthenticatedUserUtil.getCurrentUsername()
        val currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow { IllegalArgumentException("Current user not found") }

        notificationService.markNotificationsAsRead(currentUser.id!!, notificationIds)
        return ResponseEntity.ok().build()
    }
}

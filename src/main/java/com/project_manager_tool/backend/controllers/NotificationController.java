package com.project_manager_tool.backend.controllers;

import com.project_manager_tool.backend.models.Notification;
import com.project_manager_tool.backend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable int userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.OK)
    public void markAsRead(@PathVariable int notificationId) {
        notificationService.markAsRead(notificationId);
    }

    @PatchMapping("/read-all/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void markAllAsRead(@PathVariable int userId) {
        notificationService.markAllAsRead(userId);
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable int notificationId) {
        notificationService.deleteNotification(notificationId);
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendNotification(@RequestParam int userId, @RequestParam String message) {
        notificationService.sendNotificationToUserId(userId, message);
    }
}


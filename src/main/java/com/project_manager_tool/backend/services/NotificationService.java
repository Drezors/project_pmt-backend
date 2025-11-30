package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.models.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationsForUser(int userId);

    void markAsRead(int notificationId);

    void markAllAsRead(int userId);

    void deleteNotification(int notificationId);

    void sendNotificationToUserId(int userId, String message);
}

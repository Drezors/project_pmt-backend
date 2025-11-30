package com.project_manager_tool.backend.services.impl;

import com.project_manager_tool.backend.dao.NotificationRepository;
import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.models.Notification;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(int userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(int notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée"));
        notif.setRead(true);
        notificationRepository.save(notif);
    }

    public void markAllAsRead(int userId) {
        List<Notification> notifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification notif : notifs) {
            notif.setRead(true);
        }
        notificationRepository.saveAll(notifs);
    }

    public void deleteNotification(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Envoi d'une notification à partir d'un userId brut
    public void sendNotificationToUserId(int userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        sendNotification(user, message);
    }

}

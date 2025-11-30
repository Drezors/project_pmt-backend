package com.project_manager_tool.backend.services;
import com.project_manager_tool.backend.dao.NotificationRepository;
import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.models.Notification;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotificationToUserId_success() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> notificationService.sendNotificationToUserId(1, "Test message"));

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markAsRead_success() {
        Notification notification = new Notification();
        notification.setId(1);
        notification.setRead(false);

        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        assertDoesNotThrow(() -> notificationService.markAsRead(1));

        assertTrue(notification.isRead());
    }

    @Test
    void deleteNotification_success() {
        doNothing().when(notificationRepository).deleteById(1);

        assertDoesNotThrow(() -> notificationService.deleteNotification(1));
        verify(notificationRepository, times(1)).deleteById(1);
    }
}


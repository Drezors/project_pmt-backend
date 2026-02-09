package com.project_manager_tool.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_manager_tool.backend.models.Notification;
import com.project_manager_tool.backend.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void getNotifications_shouldReturnList() throws Exception {
        Notification notif = new Notification();
        notif.setId(1);
        notif.setMessage("Nouvelle notif");
        notif.setRead(false);

        when(notificationService.getNotificationsForUser(1))
                .thenReturn(Collections.singletonList(notif));

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message").value("Nouvelle notif"))
                .andExpect(jsonPath("$[0].read").value(false));

        verify(notificationService).getNotificationsForUser(1);
    }

    @Test
    void markAsRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAsRead(1);

        mockMvc.perform(patch("/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1);
    }

    @Test
    void markAllAsRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1);

        mockMvc.perform(patch("/notifications/read-all/1"))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead(1);
    }

    @Test
    void deleteNotification_shouldReturn204() throws Exception {
        doNothing().when(notificationService).deleteNotification(1);

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isNoContent());

        verify(notificationService).deleteNotification(1);
    }

    @Test
    void sendNotification_shouldReturn201() throws Exception {
        doNothing().when(notificationService).sendNotificationToUserId(1, "Coucou");

        mockMvc.perform(post("/notifications/send?userId=1&message=Coucou"))
                .andExpect(status().isCreated());

        verify(notificationService).sendNotificationToUserId(1, "Coucou");
    }
}

package com.project_manager_tool.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_manager_tool.backend.models.Notification;
import com.project_manager_tool.backend.services.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

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

        System.out.println("getNotifications_shouldReturnList PASSED");
    }

    @Test
    void markAsRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAsRead(1);

        mockMvc.perform(patch("/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1);

        System.out.println("markAsRead_shouldReturn200 PASSED");
    }

    @Test
    void markAllAsRead_shouldReturn200() throws Exception {
        doNothing().when(notificationService).markAllAsRead(1);

        mockMvc.perform(patch("/notifications/read-all/1"))
                .andExpect(status().isOk());

        verify(notificationService).markAllAsRead(1);

        System.out.println("markAllAsRead_shouldReturn200 PASSED");
    }

    @Test
    void deleteNotification_shouldReturn204() throws Exception {
        doNothing().when(notificationService).deleteNotification(1);

        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isNoContent());

        verify(notificationService).deleteNotification(1);

        System.out.println("deleteNotification_shouldReturn204 PASSED");
    }

    @Test
    void sendNotification_shouldReturn201() throws Exception {
        doNothing().when(notificationService).sendNotificationToUserId(1, "Coucou");

        mockMvc.perform(post("/notifications/send?userId=1&message=Coucou"))
                .andExpect(status().isCreated());

        verify(notificationService).sendNotificationToUserId(1, "Coucou");

        System.out.println("sendNotification_shouldReturn201 PASSED");
    }
}

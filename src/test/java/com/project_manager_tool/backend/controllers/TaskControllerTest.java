package com.project_manager_tool.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_manager_tool.backend.dto.request.TaskRequestDto;
import com.project_manager_tool.backend.models.Task;
import com.project_manager_tool.backend.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTask_shouldReturnTaskId() throws Exception {
        TaskRequestDto request = new TaskRequestDto();
        request.setName("Nouvelle tâche");
        request.setCreatedByProjectMemberId(1);
        request.setAssignedProjectMemberId(2);
        request.setPriority(Task.Priority.MEDIUM);

        when(taskService.create(eq(5), eq(1), any(TaskRequestDto.class))).thenReturn(42);

        mockMvc.perform(post("/projects/5/create-task?creatorId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("42"));
    }

    @Test
    void updateStatus_shouldReturn200() throws Exception {
        mockMvc.perform(patch("/projects/tasks/42/status?memberId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Task.Status.COMPLETED)))
                .andExpect(status().isOk());

        verify(taskService).updateStatus(42, 1, Task.Status.COMPLETED);
    }

    @Test
    void updatePriority_shouldReturn200() throws Exception {
        mockMvc.perform(patch("/projects/tasks/42/priority?adminId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Task.Priority.HIGH)))
                .andExpect(status().isOk());

        verify(taskService).updatePriority(42, 1, Task.Priority.HIGH);
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {
        doNothing().when(taskService).deleteTask(42, 1);

        mockMvc.perform(delete("/projects/tasks/42?adminId=1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(42, 1);
    }
}

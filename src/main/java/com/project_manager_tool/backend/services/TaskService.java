package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dto.request.TaskRequestDto;
import com.project_manager_tool.backend.models.Task;
import jakarta.validation.Valid;


public interface TaskService {

    int create(int projectId, int creatorId, @Valid TaskRequestDto taskRequest);

    void updateStatus(int taskId, int memberId, Task.Status newStatus);

    void updatePriority(int taskId, int adminId, Task.Priority newPriority);

    void deleteTask(int taskId, int adminId);
}
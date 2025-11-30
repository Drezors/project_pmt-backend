package com.project_manager_tool.backend.controllers;

import com.project_manager_tool.backend.dto.request.TaskRequestDto;
import com.project_manager_tool.backend.models.Task;
import com.project_manager_tool.backend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/{projectId}/create-task")
    @ResponseStatus(HttpStatus.CREATED)
    public int create(
            @PathVariable("projectId") int projectId,
            @RequestParam("creatorId") int creatorId,
            @RequestBody TaskRequestDto taskRequestDto
    ) {
        return taskService.create(projectId, creatorId, taskRequestDto);
    }

    @PatchMapping("/tasks/{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateTaskStatus(@PathVariable int taskId,
                                 @RequestParam int memberId,
                                 @RequestBody Task.Status newStatus) {
        taskService.updateStatus(taskId, memberId, newStatus);
    }

    @PatchMapping("/tasks/{taskId}/priority")
    @ResponseStatus(HttpStatus.OK)
    public void updateTaskPriority(@PathVariable int taskId,
                                   @RequestParam int adminId,
                                   @RequestBody Task.Priority newPriority) {
        taskService.updatePriority(taskId, adminId, newPriority);
    }

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable int taskId,
                           @RequestParam int adminId) {
        taskService.deleteTask(taskId, adminId);
    }

}
package com.project_manager_tool.backend.dto.request;

import com.project_manager_tool.backend.models.Task.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Integer assignedProjectMemberId;

    @NotNull
    private Integer createdByProjectMemberId;

    // Getters & setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getAssignedProjectMemberId() {
        return assignedProjectMemberId;
    }

    public void setAssignedProjectMemberId(Integer assignedProjectMemberId) {
        this.assignedProjectMemberId = assignedProjectMemberId;
    }

    public Integer getCreatedByProjectMemberId() {
        return createdByProjectMemberId;
    }

    public void setCreatedByProjectMemberId(Integer createdByProjectMemberId) {
        this.createdByProjectMemberId = createdByProjectMemberId;
    }
}
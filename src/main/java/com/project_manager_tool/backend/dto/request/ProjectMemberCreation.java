package com.project_manager_tool.backend.dto.request;

import com.project_manager_tool.backend.models.ProjectMember;
import jakarta.validation.constraints.NotBlank;

public class ProjectMemberCreation {

    @NotBlank
    private int userId;

    @NotBlank
    private ProjectMember.Role role;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ProjectMember.Role getRole() {
        return role;
    }

    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
}

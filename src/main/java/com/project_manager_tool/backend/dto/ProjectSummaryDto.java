package com.project_manager_tool.backend.dto;

import com.project_manager_tool.backend.models.ProjectMember;

public class ProjectSummaryDto {

    private Integer projectId;
    private String projectName;
    private String projectDescription;
    private ProjectMember.Role role;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public ProjectMember.Role getRole() {
        return role;
    }

    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
}

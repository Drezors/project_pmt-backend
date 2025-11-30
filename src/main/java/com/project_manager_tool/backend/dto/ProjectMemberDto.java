package com.project_manager_tool.backend.dto;

import com.project_manager_tool.backend.models.ProjectMember;

public class ProjectMemberDto {
    private Integer id;
    private ProjectMember.Role role;
    private UserDto user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProjectMember.Role getRole() {
        return role;
    }

    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}

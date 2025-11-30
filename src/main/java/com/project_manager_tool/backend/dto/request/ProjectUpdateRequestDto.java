package com.project_manager_tool.backend.dto.request;

public class ProjectUpdateRequestDto {
    private String name;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
package com.project_manager_tool.backend.dto;

import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    @Autowired
    private UserMapper userMapper;

    public ProjectDto toDto(Project project) {
        if (project == null) return null;

        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreator(userMapper.toDto(project.getOwner()));

        dto.setMembers(project.getProjectMembers().stream()
                .map(this::toMemberDto)
                .collect(Collectors.toList()));

        dto.setTasks(project.getTasks().stream()
                .map(this::toTaskDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private ProjectMemberDto toMemberDto(ProjectMember member) {
        ProjectMemberDto dto = new ProjectMemberDto();
        dto.setId(member.getId());
        dto.setRole(member.getRole());
        dto.setUser(userMapper.toDto(member.getUser()));
        return dto;
    }

    private TaskDto toTaskDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority().name());
        dto.setStatus(task.getStatus().name());

        if (task.getAssignedTo() != null) {
            dto.setAssignedUser(userMapper.toDto(task.getAssignedTo().getUser()));
        }

        if (task.getCreatedBy() != null) {
            dto.setCreatedBy(userMapper.toDto(task.getCreatedBy().getUser()));
        }

        return dto;
    }
    public ProjectSummaryDto toSummaryDto(ProjectMember member) {
        if (member == null) {
            return null;
        }
        ProjectSummaryDto dto = new ProjectSummaryDto();
        dto.setProjectId(member.getProject().getId());
        dto.setProjectName(member.getProject().getName());
        dto.setProjectDescription(member.getProject().getDescription());
        dto.setRole(member.getRole());
        return dto;
    }

}

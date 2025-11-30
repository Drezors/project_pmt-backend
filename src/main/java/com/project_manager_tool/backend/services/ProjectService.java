package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dto.ProjectDto;
import com.project_manager_tool.backend.dto.ProjectSummaryDto;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.dto.request.ProjectUpdateRequestDto;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;

import java.util.List;

public interface ProjectService {

    int create(int ownerId, Project project);

    List<ProjectMember> findAll(int memberId);
    List<ProjectSummaryDto> findAllByUserId(int userId);

    int createProjectMember(int projectId, int projectAdminId, ProjectMemberCreation ProjectMemberCreation);

    ProjectDto getProjectById(int projectId, int userId);

    void deleteProjectMember(int projectId, int userId, int deleterId);

    void updateProject(int projectId, int adminId, ProjectUpdateRequestDto updateRequest);

//    ProjectMember findById(int projectId, int memberId);
}

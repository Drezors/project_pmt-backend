package com.project_manager_tool.backend.controllers;

import com.project_manager_tool.backend.dto.ProjectDto;
import com.project_manager_tool.backend.dto.ProjectSummaryDto;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.dto.request.ProjectUpdateRequestDto;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public int create(@RequestParam("ownerId") int ownerId, @RequestBody Project project) {
        return projectService.create(ownerId, project);
    }

    @PostMapping("/{projectId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public int createProjectMember(
            @PathVariable("projectId") int projectId,
            @RequestParam("projectAdminId") int projectAdminId,
            @RequestBody  ProjectMemberCreation newProjectMember) {

        return projectService.createProjectMember(projectId, projectAdminId, newProjectMember);
    }
    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProjectMember(
            @PathVariable("projectId") int projectId,
            @PathVariable("userId") int userId,
            @RequestParam("deleterId") int deleterId) {

        projectService.deleteProjectMember(projectId, userId, deleterId);
    }


    @GetMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto getProjectById(
            @PathVariable("projectId") int projectId,
            @RequestParam("userId") int userId) {

        return projectService.getProjectById(projectId, userId);
    }


    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ProjectSummaryDto> findAll(@RequestParam("memberId") int memberId) {
        return projectService.findAllByUserId(memberId);
    }


    @PutMapping("/{projectId}/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateProject(
            @PathVariable int projectId,
            @RequestParam int adminId,
            @RequestBody ProjectUpdateRequestDto updateRequest) {
        projectService.updateProject(projectId, adminId, updateRequest);
    }



}

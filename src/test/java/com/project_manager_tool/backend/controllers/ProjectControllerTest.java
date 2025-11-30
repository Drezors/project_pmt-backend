package com.project_manager_tool.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_manager_tool.backend.dto.ProjectDto;
import com.project_manager_tool.backend.dto.ProjectSummaryDto;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.dto.request.ProjectUpdateRequestDto;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.services.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ProjectControllerTest.TestConfig.class})
class ProjectControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProjectService projectService() {
            return mock(ProjectService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturnProjectId() throws Exception {
        Project project = new Project();
        project.setName("Test Project");

        doReturn(101).when(projectService).create(eq(1), any());

        mockMvc.perform(post("/projects?ownerId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isCreated())
                .andExpect(content().string("101"));
    }

    @Test
    void createProjectMember_shouldReturnId() throws Exception {
        ProjectMemberCreation member = new ProjectMemberCreation();
        member.setUserId(2);
        member.setRole(ProjectMember.Role.MEMBER);

        doReturn(44).when(projectService).createProjectMember(eq(10), eq(1), any());

        mockMvc.perform(post("/projects/10?projectAdminId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isCreated())
                .andExpect(content().string("44"));
    }

    @Test
    void deleteProjectMember_shouldReturn204() throws Exception {
        doNothing().when(projectService).deleteProjectMember(10, 2, 1);

        mockMvc.perform(delete("/projects/10/members/2?deleterId=1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getProjectById_shouldReturnProjectDto() throws Exception {
        ProjectDto project = new ProjectDto();
        project.setId(10);
        project.setName("Test Project");

        doReturn(project).when(projectService).getProjectById(10, 1);

        mockMvc.perform(get("/projects/10?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void findAll_shouldReturnListOfSummaryDtos() throws Exception {
        ProjectSummaryDto dto = new ProjectSummaryDto();
        dto.setProjectId(1);
        dto.setProjectName("Project X");

        doReturn(Collections.singletonList(dto)).when(projectService).findAllByUserId(1);

        mockMvc.perform(get("/projects?memberId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectId").value(1))
                .andExpect(jsonPath("$[0].projectName").value("Project X"));
    }

    @Test
    void updateProject_shouldReturn200() throws Exception {
        ProjectUpdateRequestDto update = new ProjectUpdateRequestDto();
        update.setName("Updated Name");
        update.setDescription("Updated Description");

        doNothing().when(projectService).updateProject(eq(10), eq(1), any());

        mockMvc.perform(put("/projects/10/update?adminId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }
}

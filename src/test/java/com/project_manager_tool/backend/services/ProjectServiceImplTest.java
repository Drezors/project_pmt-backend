
package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.dao.ProjectRepository;
import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.dto.ProjectMapper;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.exceptions.EntityDontExistException;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.impl.ProjectServiceImpl;
import com.project_manager_tool.backend.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldReturnProjectId() {
        User owner = new User();
        owner.setId(1);

        Project project = new Project();
        project.setId(10);

        when(userService.findById(1)).thenReturn(owner);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(new ProjectMember());

        int projectId = projectService.create(1, new Project());
        assertEquals(10, projectId);
    }

    @Test
    void createProjectMember_shouldSucceed() {
        ProjectMember admin = new ProjectMember();
        admin.setRole(ProjectMember.Role.ADMIN);

        Project project = new Project();
        User newUser = new User();

        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);
        request.setRole(ProjectMember.Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(admin));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.empty());
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(newUser));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenReturn(new ProjectMember());

        int memberId = projectService.createProjectMember(1, 1, request);
        assertTrue(memberId >= 0);
    }

    @Test
    void createProjectMember_shouldFailIfAlreadyExists() {
        ProjectMember existing = new ProjectMember();
        existing.setRole(ProjectMember.Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.of(existing));

        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);
        request.setRole(ProjectMember.Role.MEMBER);

        assertThrows(IllegalArgumentException.class,
                () -> projectService.createProjectMember(1, 1, request));
    }

    @Test
    void getProjectById_shouldReturnProjectIfMember() {
        Project project = new Project();
        ProjectMember member = new ProjectMember();
        member.setProject(project);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(member));

        assertDoesNotThrow(() -> projectService.getProjectById(1, 1));
    }

    @Test
    void getProjectById_shouldFailIfNotMember() {
        Project project = new Project();

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> projectService.getProjectById(1, 1));
    }

    @Test
    void getProjectById_shouldFailIfNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityDontExistException.class,
                () -> projectService.getProjectById(1, 1));
    }
}

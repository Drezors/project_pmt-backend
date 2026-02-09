package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.dao.ProjectRepository;
import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.dto.ProjectDto;
import com.project_manager_tool.backend.dto.ProjectMapper;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.impl.ProjectServiceImpl;
import com.project_manager_tool.backend.services.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private NotificationService notificationService;

    private UserServiceImpl userService;
    private ProjectMapper projectMapper;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // pas de mock pour ces deux-là
        userService = new UserServiceImpl();
        projectMapper = new ProjectMapper() {
            @Override
            public ProjectDto toDto(Project project) {
                ProjectDto dto = new ProjectDto();
                dto.setId(project.getId());
                return dto;
            }
        };

        // création manuelle du service et injection des mocks
        projectService = new ProjectServiceImpl();
        projectService.setProjectRepository(projectRepository);
        projectService.setProjectMemberRepository(projectMemberRepository);
        projectService.setUserRepository(userRepository);
        projectService.setNotificationService(notificationService);
        projectService.setUserService(userService);
        projectService.setProjectMapper(projectMapper);
    }

    @Test
    void create_shouldReturnProjectId() {
        // Création du propriétaire factice
        User owner = new User();
        owner.setId(1);

        // Création du projet factice
        Project project = new Project();
        project.setId(10);

        // Implémentation factice de UserService
        userService = new UserServiceImpl() {
            @Override
            public User findById(int id) {
                return owner; // retourne le owner défini ci-dessus
            }
        };
        projectService.setUserService(userService);

        // Simulation des appels repository
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> {
            ProjectMember m = inv.getArgument(0);
            m.setId(100); // on fixe un id pour le membre
            return m;
        });

        // Appel réel
        int projectId = projectService.create(1, new Project());

        // Vérification
        assertEquals(10, projectId);
    }


    @Test
    void createProjectMember_shouldSucceed() {
        ProjectMember admin = new ProjectMember();
        admin.setRole(ProjectMember.Role.ADMIN);

        Project project = new Project();
        project.setId(1);

        User newUser = new User();
        newUser.setId(2);

        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);
        request.setRole(ProjectMember.Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(admin));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.empty());
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(newUser));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> {
            ProjectMember m = inv.getArgument(0);
            m.setId(42);
            return m;
        });

        int memberId = projectService.createProjectMember(1, 1, request);
        assertEquals(42, memberId);
    }

    @Test
    void getProjectById_shouldFailIfNotMember() {
        Project project = new Project();
        project.setProjectMembers(java.util.List.of());

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class,
                () -> projectService.getProjectById(1, 1));
    }

    @Test
    void getProjectById_shouldFailIfNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(1, 1));
    }
}

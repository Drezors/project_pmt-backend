package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.dao.ProjectRepository;
import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.dto.ProjectMapper;
import com.project_manager_tool.backend.dto.ProjectSummaryDto;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.dto.request.ProjectUpdateRequestDto;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.impl.ProjectServiceImpl;
import com.project_manager_tool.backend.services.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceImplTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private NotificationService notificationService;

    private UserServiceImpl userService;
    private ProjectMapper projectMapper;
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl() {
            @Override
            public User findById(int id) {
                User u = new User();
                u.setId(id);
                return u;
            }
        };

        projectMapper = new ProjectMapper() {
            @Override
            public ProjectSummaryDto toSummaryDto(ProjectMember member) {
                ProjectSummaryDto dto = new ProjectSummaryDto();
                dto.setProjectId(member.getProject().getId());
                return dto;
            }
        };

        projectService = new ProjectServiceImpl();
        projectService.setProjectRepository(projectRepository);
        projectService.setProjectMemberRepository(projectMemberRepository);
        projectService.setUserRepository(userRepository);
        projectService.setNotificationService(notificationService);
        projectService.setUserService(userService);
        projectService.setProjectMapper(projectMapper);
    }

    // ===============================================
    // create()
    // ===============================================
    @Test
    void create_shouldReturnProjectId() {
        Project project = new Project();
        project.setId(10);

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> {
            ProjectMember m = inv.getArgument(0);
            m.setId(1);
            return m;
        });

        int projectId = projectService.create(1, project);

        assertEquals(10, projectId);
    }

    @Test
    void create_shouldThrowIfOwnerNotFound() {
        projectService.setUserService(new UserServiceImpl() {
            @Override
            public User findById(int id) {
                return null;
            }
        });

        assertThrows(IllegalArgumentException.class, () -> projectService.create(1, new Project()));
    }

    // ===============================================
    // createProjectMember()
    // ===============================================
    @Test
    void createProjectMember_shouldSucceed() {
        ProjectMember admin = new ProjectMember();
        admin.setRole(ProjectMember.Role.ADMIN);
        Project project = new Project();
        project.setId(1);
        User user = new User();
        user.setId(2);

        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);
        request.setRole(ProjectMember.Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(admin));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.empty());
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> {
            ProjectMember m = inv.getArgument(0);
            m.setId(42);
            return m;
        });

        int id = projectService.createProjectMember(1, 1, request);
        assertEquals(42, id);
        verify(notificationService).sendNotificationToUserId(eq(2), contains("Vous avez été ajouté"));
    }

    @Test
    void createProjectMember_shouldFailIfNotAdmin() {
        ProjectMember nonAdmin = new ProjectMember();
        nonAdmin.setRole(ProjectMember.Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(nonAdmin));

        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);

        assertThrows(IllegalStateException.class, () ->
                projectService.createProjectMember(1, 1, request));
    }

    @Test
    void createProjectMember_shouldFailIfAlreadyMember() {
        ProjectMember admin = new ProjectMember();
        admin.setRole(ProjectMember.Role.ADMIN);
        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(admin));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.of(new ProjectMember()));

        assertThrows(IllegalStateException.class, () ->
                projectService.createProjectMember(1, 1, request));
    }

    @Test
    void createProjectMember_shouldFailIfProjectOrUserMissing() {
        ProjectMember admin = new ProjectMember();
        admin.setRole(ProjectMember.Role.ADMIN);
        ProjectMemberCreation request = new ProjectMemberCreation();
        request.setUserId(2);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(admin));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                projectService.createProjectMember(1, 1, request));
    }

    // ===============================================
    // getProjectById()
    // ===============================================
    @Test
    void getProjectById_shouldFailIfNotMember() {
        Project project = new Project();
        project.setProjectMembers(List.of());

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

    // ===============================================
    // deleteProjectMember()
    // ===============================================
    @Test
    void deleteProjectMember_shouldSucceedForAdmin() {
        User deleterUser = new User(); deleterUser.setId(1);
        ProjectMember deleter = new ProjectMember(); deleter.setUser(deleterUser); deleter.setRole(ProjectMember.Role.ADMIN);

        User memberUser = new User(); memberUser.setId(2);
        ProjectMember toDelete = new ProjectMember(); toDelete.setUser(memberUser); toDelete.setRole(ProjectMember.Role.MEMBER);

        Project project = new Project(); project.setOwner(deleterUser);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(deleter));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.of(toDelete));
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.deleteProjectMember(1, 2, 1);

        verify(projectMemberRepository).delete(toDelete);
    }

    @Test
    void deleteProjectMember_shouldFailIfNonAdmin() {
        ProjectMember deleter = new ProjectMember(); deleter.setRole(ProjectMember.Role.MEMBER);

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(deleter));

        assertThrows(IllegalStateException.class,
                () -> projectService.deleteProjectMember(1, 2, 1));
    }

    @Test
    void deleteProjectMember_shouldFailIfAdminToDeleteNotOwner() {
        User deleterUser = new User(); deleterUser.setId(1);
        ProjectMember deleter = new ProjectMember(); deleter.setUser(deleterUser); deleter.setRole(ProjectMember.Role.ADMIN);

        User memberUser = new User(); memberUser.setId(2);
        ProjectMember toDelete = new ProjectMember(); toDelete.setUser(memberUser); toDelete.setRole(ProjectMember.Role.ADMIN);

        Project project = new Project(); project.setOwner(new User()); // owner différent

        when(projectMemberRepository.findByProjectIdAndUserId(1, 1)).thenReturn(Optional.of(deleter));
        when(projectMemberRepository.findByProjectIdAndUserId(1, 2)).thenReturn(Optional.of(toDelete));
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        assertThrows(IllegalStateException.class,
                () -> projectService.deleteProjectMember(1, 2, 1));
    }

    // ===============================================
    // updateProject()
    // ===============================================
    @Test
    void updateProject_shouldSucceed() {
        User owner = new User(); owner.setId(1);
        Project project = new Project(); project.setId(10); project.setOwner(owner); project.setName("Old Name");

        ProjectMember member = new ProjectMember(); member.setRole(ProjectMember.Role.ADMIN);

        ProjectUpdateRequestDto dto = new ProjectUpdateRequestDto();
        dto.setName("New Name");
        dto.setDescription("Desc");

        when(projectRepository.findById(10)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndUserId(10, 1)).thenReturn(Optional.of(member));

        projectService.updateProject(10, 1, dto);

        assertEquals("New Name", project.getName());
        verify(notificationService).sendNotificationToUserId(eq(1), contains("mis à jour"));
    }

    @Test
    void updateProject_shouldFailIfNotAdmin() {
        Project project = new Project();
        ProjectMember member = new ProjectMember(); member.setRole(ProjectMember.Role.MEMBER);
        ProjectUpdateRequestDto dto = new ProjectUpdateRequestDto();

        when(projectRepository.findById(10)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findByProjectIdAndUserId(10, 1)).thenReturn(Optional.of(member));

        assertThrows(IllegalStateException.class,
                () -> projectService.updateProject(10, 1, dto));
    }

    @Test
    void updateProject_shouldFailIfProjectNotFound() {
        ProjectUpdateRequestDto dto = new ProjectUpdateRequestDto();
        when(projectRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> projectService.updateProject(10, 1, dto));
    }

    @Test
    void findAll_shouldThrowIfUserNotFound() {
        userService = new UserServiceImpl() {
            @Override
            public User findById(int id) { return null; } // simulate user not found
        };
        projectService.setUserService(userService);

        assertThrows(IllegalArgumentException.class,
                () -> projectService.findAll(999));
    }

    @Test
    void findAll_shouldReturnEmptyListIfNoMembers() {
        User user = new User();
        user.setId(1);
        userService = new UserServiceImpl() {
            @Override
            public User findById(int id) { return user; }
        };
        projectService.setUserService(userService);

        when(projectMemberRepository.findAll()).thenReturn(java.util.List.of());

        assertTrue(projectService.findAll(1).isEmpty());
    }

    @Test
    void findAll_shouldReturnFilteredMembers() {
        User user = new User();
        user.setId(1);
        User otherUser = new User();
        otherUser.setId(2);

        ProjectMember m1 = new ProjectMember();
        m1.setUser(user);
        ProjectMember m2 = new ProjectMember();
        m2.setUser(otherUser);

        userService = new UserServiceImpl() {
            @Override
            public User findById(int id) { return user; }
        };
        projectService.setUserService(userService);

        when(projectMemberRepository.findAll()).thenReturn(java.util.List.of(m1, m2));

        var members = projectService.findAll(1);
        assertEquals(1, members.size());
        assertEquals(user.getId(), members.get(0).getUser().getId());
    }
}

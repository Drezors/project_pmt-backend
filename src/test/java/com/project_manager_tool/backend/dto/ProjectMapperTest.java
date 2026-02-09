package com.project_manager_tool.backend.dto;

import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.Task;
import com.project_manager_tool.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectMapperTest {

    private ProjectMapper projectMapper;
    private UserMapper userMapper;


    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(); // instanciation réelle
        projectMapper = new ProjectMapper() {
            {
                this.userMapper = ProjectMapperTest.this.userMapper;
            }
        };
    }

    @Test
    void toDto_shouldReturnNullForNullProject() {
        assertNull(projectMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapProjectWithOwnerAndMembersAndTasks() {
        // Projet
        Project project = new Project();
        project.setId(1);
        project.setName("Test Project");
        project.setDescription("Description");

        // Owner
        User owner = new User();
        owner.setId(10);
        project.setOwner(owner);

        // Members
        ProjectMember member = new ProjectMember();
        member.setId(20);
        User memberUser = new User();
        memberUser.setId(30);
        member.setUser(memberUser);
        member.setRole(ProjectMember.Role.MEMBER);
        project.setProjectMembers(List.of(member));

        // Tasks
        Task task1 = new Task();
        task1.setId(100);
        task1.setName("Task 1");
        task1.setPriority(Task.Priority.LOW);
        task1.setStatus(Task.Status.PENDING);

        Task task2 = new Task();
        task2.setId(101);
        task2.setName("Task 2");
        task2.setPriority(Task.Priority.HIGH);
        task2.setStatus(Task.Status.COMPLETED);
        User assignedUser = new User(); assignedUser.setId(40);
        User createdUser = new User(); createdUser.setId(50);
        task2.setAssignedTo(new ProjectMember()); task2.getAssignedTo().setUser(assignedUser);
        task2.setCreatedBy(new ProjectMember()); task2.getCreatedBy().setUser(createdUser);

        project.setTasks(List.of(task1, task2));

        // Mapping réel sans mock
        ProjectDto dto = projectMapper.toDto(project);

        // Vérifications
        assertEquals(1, dto.getId());
        assertEquals("Test Project", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertNotNull(dto.getCreator());
        assertEquals(10, dto.getCreator().getId());

        assertEquals(1, dto.getMembers().size());
        assertEquals(30, dto.getMembers().get(0).getUser().getId());
        assertEquals(ProjectMember.Role.MEMBER, dto.getMembers().get(0).getRole());

        assertEquals(2, dto.getTasks().size());
        assertEquals(100, dto.getTasks().get(0).getId());
        assertNull(dto.getTasks().get(0).getAssignedUser());
        assertNull(dto.getTasks().get(0).getCreatedBy());

        assertEquals(101, dto.getTasks().get(1).getId());
        assertEquals(40, dto.getTasks().get(1).getAssignedUser().getId());
        assertEquals(50, dto.getTasks().get(1).getCreatedBy().getId());
    }


    @Test
    void toDto_shouldHandleNullOwnerAndEmptyMembersAndTasks() {
        Project project = new Project();
        project.setId(2);
        project.setProjectMembers(List.of());
        project.setTasks(List.of());
        project.setOwner(null);

        ProjectDto dto = projectMapper.toDto(project);

        assertEquals(2, dto.getId());
        assertNull(dto.getCreator());
        assertTrue(dto.getMembers().isEmpty());
        assertTrue(dto.getTasks().isEmpty());
    }

    @Test
    void toSummaryDto_shouldReturnNullForNullMember() {
        assertNull(projectMapper.toSummaryDto(null));
    }

    @Test
    void toSummaryDto_shouldMapProjectMember() {
        Project project = new Project();
        project.setId(1);
        project.setName("Proj");
        project.setDescription("Desc");

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setRole(ProjectMember.Role.ADMIN);

        var dto = projectMapper.toSummaryDto(member);

        assertEquals(1, dto.getProjectId());
        assertEquals("Proj", dto.getProjectName());
        assertEquals("Desc", dto.getProjectDescription());
        assertEquals(ProjectMember.Role.ADMIN, dto.getRole());
    }
}

package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.dao.ProjectRepository;
import com.project_manager_tool.backend.dao.TaskRepository;
import com.project_manager_tool.backend.dto.request.TaskRequestDto;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.Task;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_success() {
        int projectId = 1;
        int creatorId = 1;

        TaskRequestDto request = new TaskRequestDto();
        request.setName("Task name");
        request.setPriority(Task.Priority.MEDIUM);
        request.setCreatedByProjectMemberId(creatorId);
        request.setAssignedProjectMemberId(2);

        Project project = new Project();

        ProjectMember creator = new ProjectMember();
        creator.setId(creatorId);
        creator.setRole(ProjectMember.Role.ADMIN);
        User creatorUser = new User();
        creatorUser.setId(creatorId);
        creator.setUser(creatorUser);

        ProjectMember assignee = new ProjectMember();
        assignee.setId(2);
        User assigneeUser = new User();
        assigneeUser.setId(2);
        assignee.setUser(assigneeUser);

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, creatorId)).thenReturn(Optional.of(creator));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMemberRepository.findById(2)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(42);
            return t;
        });

        int taskId = taskService.create(projectId, creatorId, request);

        assertEquals(42, taskId);
    }


    @Test
    void updateStatus_shouldWorkIfAdmin() {
        int taskId = 10;
        int memberId = 1;

        Task task = new Task();
        task.setId(taskId);
        task.setStatus(Task.Status.PENDING);

        ProjectMember member = new ProjectMember();
        member.setId(memberId);
        member.setRole(ProjectMember.Role.ADMIN);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        assertDoesNotThrow(() -> taskService.updateStatus(taskId, memberId, Task.Status.COMPLETED));
        assertEquals(Task.Status.COMPLETED, task.getStatus());
    }

    @Test
    void updatePriority_shouldWorkIfAdmin() {
        int taskId = 11;
        int adminId = 1;

        Task task = new Task();
        task.setId(taskId);
        task.setPriority(Task.Priority.LOW);

        ProjectMember admin = new ProjectMember();
        admin.setId(adminId);
        admin.setRole(ProjectMember.Role.ADMIN);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectMemberRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        assertDoesNotThrow(() -> taskService.updatePriority(taskId, adminId, Task.Priority.HIGH));
        assertEquals(Task.Priority.HIGH, task.getPriority());
    }

    @Test
    void deleteTask_shouldWorkIfAdmin() {
        int taskId = 20;
        int adminId = 1;

        ProjectMember admin = new ProjectMember();
        admin.setId(adminId);
        admin.setRole(ProjectMember.Role.ADMIN);

        Task task = new Task();
        task.setId(taskId);


        when(projectMemberRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(taskId);

        assertDoesNotThrow(() -> taskService.deleteTask(taskId, adminId));
        verify(taskRepository, times(1)).deleteById(taskId);
    }

}
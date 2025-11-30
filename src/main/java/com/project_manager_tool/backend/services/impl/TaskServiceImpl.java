package com.project_manager_tool.backend.services.impl;

import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.dao.ProjectRepository;
import com.project_manager_tool.backend.dao.TaskRepository;
import com.project_manager_tool.backend.dto.TaskDto;
import com.project_manager_tool.backend.dto.request.TaskRequestDto;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.Task;
import com.project_manager_tool.backend.services.NotificationService;
import com.project_manager_tool.backend.services.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    public int create(int projectId, int creatorId, TaskRequestDto taskDto) {
        Optional<ProjectMember> creatorOpt = projectMemberRepository
                .findByProjectIdAndUserId(projectId, creatorId);

        if (creatorOpt.isEmpty() || creatorOpt.get().getRole() != ProjectMember.Role.ADMIN) {
            throw new IllegalStateException("Seuls les membres ADMIN peuvent créer des tâches.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé."));

        ProjectMember assignee = projectMemberRepository.findById(taskDto.getAssignedProjectMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Membre assigné introuvable."));

        // 4. Crée la tâche
        Task task = new Task();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setStatus(Task.Status.PENDING); // valeur par défaut
        task.setProject(project);
        task.setAssignedTo(assignee);
        task.setCreatedBy(creatorOpt.get());

        taskRepository.save(task);


        if (assignee != null) {
            notificationService.sendNotificationToUserId(
                    assignee.getUser().getId(),
                    "Une nouvelle tâche \"" + task.getName() + "\" vous a été assignée dans le projet \"" + project.getName() + "\"."
            );
        }



        return task.getId();
    }

    @Override
    public void updateStatus(int taskId, int memberId, Task.Status newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche introuvable"));

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Membre non trouvé"));

        boolean isAdmin = member.getRole() == ProjectMember.Role.ADMIN;
        boolean isAssignee = task.getAssignedTo() != null &&
                task.getAssignedTo().getId().equals(member.getId());

        if (!isAdmin && !isAssignee) {
            throw new IllegalStateException("Seul l'assigné ou un admin peut changer le statut.");
        }

        task.setStatus(newStatus);

        if (task.getCreatedBy() != null) {
            notificationService.sendNotificationToUserId(
                    task.getCreatedBy().getId(),
                    "Le statut de la tâche \"" + task.getName() + "\" a été changé en \"" + newStatus.name() + "\"."
            );
        }

        taskRepository.save(task);
    }

    @Override
    public void updatePriority(int taskId, int projectMemberId, Task.Priority newPriority) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche introuvable"));

        ProjectMember admin = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new IllegalStateException("Membre non trouvé"));

        if (admin.getRole() != ProjectMember.Role.ADMIN) {
            throw new IllegalStateException("Seul un administrateur peut modifier la priorité.");
        }

        task.setPriority(newPriority);
        taskRepository.save(task);
    }

    @Override
    public void deleteTask(int taskId, int projectMemberId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche introuvable"));

        ProjectMember admin = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new IllegalStateException("Membre non trouvé"));

        if (admin.getRole() != ProjectMember.Role.ADMIN) {
            throw new IllegalStateException("Seul un administrateur peut supprimer une tâche.");
        }

        taskRepository.delete(task);
    }


}

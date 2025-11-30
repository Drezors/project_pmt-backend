package com.project_manager_tool.backend.services.impl;


import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.dao.ProjectRepository;
import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.dto.ProjectDto;
import com.project_manager_tool.backend.dto.ProjectMapper;
import com.project_manager_tool.backend.dto.ProjectSummaryDto;
import com.project_manager_tool.backend.dto.request.ProjectMemberCreation;
import com.project_manager_tool.backend.dto.request.ProjectUpdateRequestDto;
import com.project_manager_tool.backend.exceptions.EntityDontExistException;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.NotificationService;
import com.project_manager_tool.backend.services.ProjectMemberService;
import com.project_manager_tool.backend.services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectMapper projectMapper;


    @Override
    public int create(int ownerId, Project project) {
        User owner = userService.findById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Owner with ID " + ownerId + " not found.");
        }

        project.setOwner(owner);

        Project newProject = projectRepository.save(project);

        ProjectMember newMember = new ProjectMember();
        newMember.setProject(newProject);
        newMember.setUser(owner);
        newMember.setRole(ProjectMember.Role.ADMIN);

        projectMemberRepository.save(newMember);

        return newProject.getId();
    }

    @Override
    public int createProjectMember(int projectId, int projectAdminId, ProjectMemberCreation newMemberUserId) {
        // Vérification si l'admin existe bien et a le rôle ADMIN
        Optional<ProjectMember> adminMember = projectMemberRepository
                .findByProjectIdAndUserId(projectId, projectAdminId);

        if (adminMember.isEmpty() || adminMember.get().getRole() != ProjectMember.Role.ADMIN) {
            throw new IllegalStateException("Only an admin can add new project members");
        }

        // Vérification si le membre existe déjà dans le projet
        boolean isAlreadyMember = projectMemberRepository
                .findByProjectIdAndUserId(projectId, newMemberUserId.getUserId())
                .isPresent();

        if (isAlreadyMember) {
            throw new IllegalStateException("User is already a member of this project");
        }

        // Création du nouveau membre si la vérification est OK
        ProjectMember newMember = new ProjectMember();
        newMember.setRole(newMemberUserId.getRole());

        // Associer le projet
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        newMember.setProject(project);

        // Associer l'utilisateur
        User user = userRepository.findById(newMemberUserId.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        newMember.setUser(user);

        projectMemberRepository.save(newMember);


        notificationService.sendNotificationToUserId(
                newMember.getUser().getId(),
                "Vous avez été ajouté au projet ID " + projectId + " en tant que " + newMember.getRole() + "."
        );

        return newMember.getId();
    }



    @Override
    public List<ProjectMember> findAll(int memberId) {
        // Vérifie si l'utilisateur avec memberId existe
        User user = userService.findById(memberId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + memberId + " not found.");
        }

        // Récupérer les ProjectMember associés à cet utilisateur
        List<ProjectMember> filteredMembers = new ArrayList<>();
        for (ProjectMember projectMember : projectMemberRepository.findAll()) {
            if (projectMember.getUser() != null && projectMember.getUser().getId().equals(memberId)) {
                filteredMembers.add(projectMember);
            }
        }

        return filteredMembers;
    }

    @Override
    public List<ProjectSummaryDto> findAllByUserId(int userId) {
        // Récupère tous les ProjectMember associés à un userId
        List<ProjectMember> projectMembers = projectMemberRepository.findByUserId(userId);

        return projectMembers.stream()
                .map(projectMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto getProjectById(int projectId, int userId) {
        // Vérification si le projet existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        // Vérification si l'utilisateur est membre du projet
        boolean isMember = project.getProjectMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));

        if (!isMember) {
            throw new IllegalStateException("User does not have access to this project");
        }

        // Si tout est bon, on convertit le projet en DTO via le mapper
        return projectMapper.toDto(project);
    }

    @Override
    public void deleteProjectMember(int projectId, int userId, int deleterId) {
        // ✅ Vérifie si le ProjectMember qui fait la suppression existe
        ProjectMember deleter = projectMemberRepository
                .findByProjectIdAndUserId(projectId, deleterId)
                .orElseThrow(() -> new IllegalStateException("Vous n'êtes pas membre de ce projet"));

        // ✅ Vérifie si le ProjectMember à supprimer existe
        ProjectMember memberToDelete = projectMemberRepository
                .findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new IllegalStateException("Le membre à supprimer n'existe pas"));

        // ✅ Seul un ADMIN peut supprimer un autre membre
        if (deleter.getRole() != ProjectMember.Role.ADMIN) {
            throw new IllegalStateException("Seul un admin peut supprimer un membre");
        }

        // ✅ Si le membre à supprimer est ADMIN, seul le créateur du projet peut le supprimer
        if (memberToDelete.getRole() == ProjectMember.Role.ADMIN) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("Projet introuvable"));

            if (project.getOwner().getId() != deleter.getUser().getId()) {
                throw new IllegalStateException("Seul le créateur du projet peut supprimer un admin");
            }
        }

        // ✅ Suppression de l'utilisateur
        projectMemberRepository.delete(memberToDelete);
    }

    @Override
    public void updateProject(int projectId, int adminId, ProjectUpdateRequestDto request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Projet introuvable"));

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, adminId)
                .orElseThrow(() -> new IllegalStateException("Membre non trouvé"));

        if (member.getRole() != ProjectMember.Role.ADMIN) {
            throw new IllegalStateException("Seuls les admins peuvent modifier un projet");
        }

        if (request.getName() != null) project.setName(request.getName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());


        notificationService.sendNotificationToUserId(
                project.getOwner().getId(),
                "Votre projet \"" + project.getName() + "\" a été mis à jour."
        );

        projectRepository.save(project);

    }


}
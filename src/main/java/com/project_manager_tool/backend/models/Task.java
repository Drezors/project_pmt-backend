package com.project_manager_tool.backend.models;

import jakarta.persistence.*;

@Table(name = "task")
@Entity
public class Task {

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;
    private Priority priority;
    private Status status;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private ProjectMember assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private ProjectMember createdBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectMember getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(ProjectMember assignedTo) {
        this.assignedTo = assignedTo;
    }

    public ProjectMember getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ProjectMember createdBy) {
        this.createdBy = createdBy;
    }
}
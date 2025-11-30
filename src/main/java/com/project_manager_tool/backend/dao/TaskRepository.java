package com.project_manager_tool.backend.dao;

import com.project_manager_tool.backend.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}

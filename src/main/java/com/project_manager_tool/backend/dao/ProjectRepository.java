package com.project_manager_tool.backend.dao;

import com.project_manager_tool.backend.models.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project,Integer> {
}

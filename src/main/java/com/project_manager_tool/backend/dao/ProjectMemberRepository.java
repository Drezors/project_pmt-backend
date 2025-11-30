package com.project_manager_tool.backend.dao;

import com.project_manager_tool.backend.models.ProjectMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends CrudRepository<ProjectMember,Integer> {

    Optional<ProjectMember> findByProjectIdAndUserId(int projectId, int projectAdminId);

    @Query("SELECT pm FROM ProjectMember pm WHERE pm.user.id = :userId")
    List<ProjectMember> findByUserId(@Param("userId") int userId);
}

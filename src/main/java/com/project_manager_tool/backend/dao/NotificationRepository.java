package com.project_manager_tool.backend.dao;

import com.project_manager_tool.backend.models.Notification;
import com.project_manager_tool.backend.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends CrudRepository<Notification,Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(int userId);
}

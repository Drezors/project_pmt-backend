package com.project_manager_tool.backend.services;

import com.project_manager_tool.backend.dto.UserDto;
import com.project_manager_tool.backend.dto.request.UserLogin;
import com.project_manager_tool.backend.models.User;

import java.util.List;

public interface UserService {


     List<UserDto> getAllUsers();

     User findById(int id);

     int create(User user);

     void update(int id, User user);

     void updatePartial(User existingUser, User newUser);

     void delete(int id);

     int login(UserLogin userLogin);
}

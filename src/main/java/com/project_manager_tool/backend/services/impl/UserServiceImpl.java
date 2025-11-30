package com.project_manager_tool.backend.services.impl;

import com.project_manager_tool.backend.dao.UserRepository;

import com.project_manager_tool.backend.dto.UserDto;
import com.project_manager_tool.backend.dto.UserMapper;
import com.project_manager_tool.backend.dto.request.UserLogin;
import com.project_manager_tool.backend.exceptions.EntityDontExistException;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.UserService;
//import com.project_manager_tool.backend.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }


    @Override
    public User findById(int id) {

        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()) {
            return user.get();
        }

        throw new EntityDontExistException("User not found");
    }

    @Override
    public int create(User user) {
        return userRepository.save(user).getId();
    }

    @Override
    public int login(UserLogin userLogin) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(userLogin.getEmail()));

        if (user.isPresent()) {
            if (user.get().getPassword().equals(userLogin.getPassword())) {
                return user.get().getId();
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        }

        throw new EntityDontExistException("User not found");
    }


    @Override
    public void update(int id, User user) {
        user.setId(id);
        userRepository.save(user);
    }

    @Override
    public void updatePartial(User existingUser, User newUser) {
        if(newUser.getPassword() != null) {
            existingUser.setPassword(newUser.getPassword());
        }

        if(newUser.getEmail() != null) {
            existingUser.setEmail(newUser.getEmail());
        }

        if(newUser.getUsername()!= null) {
            existingUser.setUsername(newUser.getUsername());
        }

        userRepository.save(newUser);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }


}

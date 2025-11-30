package com.project_manager_tool.backend.services;


import com.project_manager_tool.backend.dao.UserRepository;
import com.project_manager_tool.backend.dto.UserMapper;
import com.project_manager_tool.backend.dto.request.UserLogin;
import com.project_manager_tool.backend.exceptions.EntityDontExistException;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldReturnId() {
        User user = new User();
        user.setId(1);
        when(userRepository.save(any(User.class))).thenReturn(user);

        int id = userService.create(user);
        assertEquals(1, id);
    }

    @Test
    void findById_shouldReturnUser() {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User found = userService.findById(1);
        assertEquals(1, found.getId());
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityDontExistException.class, () -> userService.findById(1));
    }

    @Test
    void login_shouldReturnUserId() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("pass");

        UserLogin login = new UserLogin();
        login.setEmail("test@example.com");
        login.setPassword("pass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        int id = userService.login(login);
        assertEquals(1, id);
    }

    @Test
    void login_shouldThrowIfWrongPassword() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("wrong");

        UserLogin login = new UserLogin();
        login.setEmail("test@example.com");
        login.setPassword("pass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> userService.login(login));
    }

    @Test
    void delete_shouldCallRepository() {
        doNothing().when(userRepository).deleteById(1);
        assertDoesNotThrow(() -> userService.delete(1));
        verify(userRepository, times(1)).deleteById(1);
    }
}

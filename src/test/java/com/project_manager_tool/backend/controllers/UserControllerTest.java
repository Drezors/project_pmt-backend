package com.project_manager_tool.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_manager_tool.backend.dto.UserDto;
import com.project_manager_tool.backend.dto.request.UserLogin;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_shouldReturn201() throws Exception {
        User user = new User();
        user.setUsername("JohnDoe");
        user.setEmail("new@example.com");
        user.setPassword("pass");

        when(userService.create(any(User.class))).thenReturn(99);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("99"));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    void login_shouldReturn201() throws Exception {
        UserLogin login = new UserLogin();
        login.setEmail("test@example.com");
        login.setPassword("pass");

        when(userService.login(any(UserLogin.class))).thenReturn(1);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(userService, times(1)).login(any(UserLogin.class));
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        UserDto dto1 = new UserDto();
        dto1.setId(1);
        dto1.setUsername("User1");
        UserDto dto2 = new UserDto();
        dto2.setId(2);
        dto2.setUsername("User2");

        when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("User1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("User2"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void findById_shouldReturn200() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("JeanDupont");

        when(userService.findById(1)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("JeanDupont"));

        verify(userService, times(1)).findById(1);
    }

    @Test
    void update_shouldReturn200() throws Exception {
        User updatedUser = new User();
        updatedUser.setUsername("Updated");

        doNothing().when(userService).update(anyInt(), any(User.class));
        when(userService.findById(1)).thenReturn(new User());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk());

        verify(userService, times(1)).update(eq(1), any(User.class));
    }

    @Test
    void delete_shouldReturn200() throws Exception {
        doNothing().when(userService).delete(1);
        when(userService.findById(1)).thenReturn(new User());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1);
    }
}

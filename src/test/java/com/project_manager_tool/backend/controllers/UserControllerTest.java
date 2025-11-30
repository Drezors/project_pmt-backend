package com.project_manager_tool.backend.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_manager_tool.backend.dto.request.UserLogin;
import com.project_manager_tool.backend.dto.request.UserRegister;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    void login_shouldReturnUserId() throws Exception {
        UserLogin login = new UserLogin();
        login.setEmail("test@example.com");
        login.setPassword("pass");

        when(userService.login(any(UserLogin.class))).thenReturn(1);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void register_shouldReturn201() throws Exception {
        UserRegister request = new UserRegister();
        request.setEmail("new@example.com");
        request.setPassword("pass");
        request.setUsername("JohnDoe");

        User user = new User();
        user.setId(99);

        when(userService.create(any(User.class))).thenReturn(99);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("99"));
    }

    @Test
    void findById_shouldReturnUser() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("JeanDupont");

        when(userService.findById(1)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("JeanDupont"));
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        doNothing().when(userService).delete(1);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1);
    }
}

package com.project_manager_tool.backend.controllers;

import com.project_manager_tool.backend.dto.UserDto;
import com.project_manager_tool.backend.dto.request.UserLogin;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService ;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public int create(@RequestBody User user) {
        return userService.create(user);
    }

    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.CREATED)
    public int login(@RequestBody UserLogin userLogin) {
        return userService.login(userLogin);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public User findById(@PathVariable("id") int id) {
        return userService.findById(id);

    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void update(@PathVariable int id, @RequestBody User user) {
        userService.findById(id);
        userService.update(id, user);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void updatePartial(@PathVariable int id, @RequestBody User newUser) {
        User existingUser = userService.findById(id);
        userService.updatePartial( existingUser, newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(@PathVariable int id) {
        userService.findById(id);
        userService.delete(id);

    }

}

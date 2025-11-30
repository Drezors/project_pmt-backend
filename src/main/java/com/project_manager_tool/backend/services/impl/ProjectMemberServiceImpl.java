package com.project_manager_tool.backend.services.impl;

import com.project_manager_tool.backend.dao.ProjectMemberRepository;
import com.project_manager_tool.backend.exceptions.EntityDontExistException;
import com.project_manager_tool.backend.models.Project;
import com.project_manager_tool.backend.models.ProjectMember;
import com.project_manager_tool.backend.models.User;
import com.project_manager_tool.backend.services.ProjectMemberService;
import com.project_manager_tool.backend.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ProjectServiceImpl projectService;






}

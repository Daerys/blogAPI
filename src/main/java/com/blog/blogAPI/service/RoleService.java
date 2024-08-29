package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.Role;
import com.blog.blogAPI.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByName(String role) {
        return roleRepository.findByName(role);
    }
}

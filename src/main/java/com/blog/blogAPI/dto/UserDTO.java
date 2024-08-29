package com.blog.blogAPI.dto;

import com.blog.blogAPI.domain.User;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private String email;
    private Set<RoleDTO> roles;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(role -> new RoleDTO(role.getName())).collect(Collectors.toSet());
    }

}

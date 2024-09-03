package com.blog.blogAPI.dto;

import com.blog.blogAPI.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDTO {

    private Long id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    private Set<RoleDTO> roles;

    private boolean isEnabled;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(role -> new RoleDTO(role.getName())).collect(Collectors.toSet());
        this.isEnabled = user.isEnabled();
    }

}

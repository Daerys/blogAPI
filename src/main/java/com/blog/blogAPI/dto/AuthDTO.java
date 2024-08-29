package com.blog.blogAPI.dto;

import lombok.Data;

@Data
public class AuthDTO {
    private String email;
    private String password;

    public String getPassword() {
        System.err.println(password);
        return password;
    }
}

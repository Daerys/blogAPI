package com.blog.blogAPI.controller;


import com.blog.blogAPI.domain.Role;
import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.AuthDTO;
import com.blog.blogAPI.dto.UserDTO;
import com.blog.blogAPI.security.JWTUtils;
import com.blog.blogAPI.service.RoleService;
import com.blog.blogAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.blog.blogAPI.utils.ControllerUtils.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDTO authDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        if (userService.existsByEmail(authDTO.getEmail())) {
            return new ResponseEntity<>("This email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User(authDTO.getEmail(), passwordEncoder.encode(authDTO.getPassword()));

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleService.findByName("USER").get());
        user.setRoles(roleSet);

        userService.save(user);
        UserDTO userDTO = new UserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        System.err.println(authDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authDTO.getEmail(),
                        authDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, String> tokens = new HashMap<>();
        User user = userService.findByEmail(authDTO.getEmail());
        tokens.put("accessToken", jwtUtils.generateAccessTokenFromUsername(user));
        tokens.put("refreshToken", jwtUtils.generateRefreshTokenFromUsername(user));
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

}

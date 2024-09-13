package com.blog.blogAPI.controller;

import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.UserDTO;
import com.blog.blogAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.blog.blogAPI.utils.ExceptionMessagesUtils.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isAccountOwner(authentication, #userId)")
    @PostMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        if (updatedUser == null) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN') or @securityService.isAccountOwner(authentication, #userId)")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        userService.delete(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

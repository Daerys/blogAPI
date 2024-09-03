package com.blog.blogAPI.controller;

import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.UserDTO;
import com.blog.blogAPI.service.SecurityService;
import com.blog.blogAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.blog.blogAPI.utils.ExceptionMessagesUtils.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isAccountOwner(authentication, #userId)")
    @PostMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        User user = userService.findById(userId);
        if (user == null) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        if (securityService.hasBanPrivileges(SecurityContextHolder.getContext().getAuthentication())
                && userDTO.isEnabled() != user.isEnabled()) user.setEnabled(userDTO.isEnabled());
        else if (userDTO.isEnabled() != user.isEnabled()) return new ResponseEntity<>("You have no permission to perform this action.", HttpStatus.FORBIDDEN);
        user.setEmail(userDTO.getEmail());
        userService.save(user);
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
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

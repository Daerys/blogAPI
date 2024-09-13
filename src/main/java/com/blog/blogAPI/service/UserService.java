package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.UserDTO;
import com.blog.blogAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.blog.blogAPI.utils.ExceptionMessagesUtils.NO_AUTHORITY_ERROR;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityService securityService;

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean existsById(Long userId) {
        return findById(userId) != null;
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = findById(userId);
        if (user == null) return null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userDTO.isEnabled() != user.isEnabled()) {
            if (securityService.hasBanPrivileges(authentication)) user.setEnabled(userDTO.isEnabled());
            throw new AccessDeniedException(NO_AUTHORITY_ERROR);
        }
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
        return new UserDTO(user);
    }
}

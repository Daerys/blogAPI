package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.blog.blogAPI.utils.ExceptionMessagesUtils.*;

@Service
public class SecurityService {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    public boolean isPostAuthor(Authentication authentication, Long postId) {
        Post post = postService.findById(postId);
        return isAuthor(authentication, post == null, NO_POST_ERROR, post);
    }

    public boolean isCommentAuthor(Authentication authentication, Long commentId) {
        Comment comment = commentService.findById(commentId);
        return isAuthor(authentication, comment == null, NO_COMMENT_ERROR, comment);
    }

    private <E extends HasAuthor> boolean isAuthor(Authentication authentication, boolean b, String noPostError, E entity) {
        User author = entity.getAuthor();
        if (b) {
            throw new EntityNotFoundException(noPostError);
        }
        User authUser = (User) authentication.getPrincipal();
        try {
            return author.getId().equals(authUser.getId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(NO_USER_ID_MESSAGE);
        }
    }

    public boolean isAccountOwner(Authentication authentication, Long userId) {
        User user = userService.findById(userId);
        User authUser = (User) authentication.getPrincipal();
        return user.getId().equals(authUser.getId());
    }

    public boolean hasBanPrivileges(Authentication authentication) {
        return ((User) authentication.getPrincipal())
                .getAuthorities()
                .stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
    }
}

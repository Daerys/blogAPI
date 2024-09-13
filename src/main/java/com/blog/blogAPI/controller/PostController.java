package com.blog.blogAPI.controller;

import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.PostDTO;
import com.blog.blogAPI.service.PostService;
import com.blog.blogAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.blog.blogAPI.utils.ControllerUtils.*;
import static com.blog.blogAPI.utils.ExceptionMessagesUtils.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDTO post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(post.getAuthorId());
        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        PostDTO createdPost = postService.createPost(user, post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isPostAuthor(authentication, #post.id)")
    @PostMapping("/update")
    public ResponseEntity<?> updatePost(@Valid @RequestBody PostDTO post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(post.getAuthorId());
        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        PostDTO updatedPost = postService.updatePost(user, post);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isPostAuthor(authentication, #post.id)")
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody PostDTO post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(post.getAuthorId());

        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        if (!postService.deletePost(post)) {
            return new ResponseEntity<>(UNABLE_TO_DELETE_POST_ERROR, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("Post has been deleted successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllPostsByUser(@PathVariable Long userId) {
        if (!userService.existsById(userId)) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        List<PostDTO> posts = postService.findByOwnerId(userId).stream().map(PostDTO::new).toList();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        if (!postService.existsById(postId)) return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new PostDTO(postService.findById(postId)), HttpStatus.OK);
    }
}

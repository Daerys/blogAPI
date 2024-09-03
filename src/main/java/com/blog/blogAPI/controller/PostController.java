package com.blog.blogAPI.controller;

import com.blog.blogAPI.domain.Post;
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
        Post postDB = new Post(post.getTitle(), post.getContent(), user);
        postService.save(postDB);
        post.setId(postDB.getId());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isPostAuthor(authentication, #post.id)")
    @PostMapping("/update")
    public ResponseEntity<?> updatePost(@Valid @RequestBody PostDTO post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);

        User user = userService.findById(post.getAuthorId());
        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        Post postDB = new Post(post.getTitle(), post.getContent(), user);
        postService.save(postDB);
        post.setId(postDB.getId());
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isPostAuthor(authentication, #post.id)")
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody PostDTO post, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(post.getAuthorId());

        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        try {
            if (!postService.existById(post.getId())) {
                return new ResponseEntity<>(NO_POST_ERROR, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(NO_POST_ID_MESSAGE);
        }

        postService.delete(postService.findById(post.getId()));
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

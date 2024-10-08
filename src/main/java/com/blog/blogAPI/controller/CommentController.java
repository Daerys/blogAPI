package com.blog.blogAPI.controller;

import com.blog.blogAPI.domain.Comment;
import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.CommentDTO;
import com.blog.blogAPI.service.CommentService;
import com.blog.blogAPI.service.PostService;
import com.blog.blogAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.blog.blogAPI.utils.ControllerUtils.*;
import static com.blog.blogAPI.utils.ExceptionMessagesUtils.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDTO comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(comment.getAuthorId());
        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        CommentDTO createdComment = commentService.createComment(user, postService.findById(comment.getPostId()), comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PreAuthorize("@securityService.isCommentAuthor(authentication, #comment.id)")
    @PostMapping("/update")
    public ResponseEntity<?> updatePost(@Valid @RequestBody CommentDTO comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(comment.getAuthorId());
        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        CommentDTO updatedComment = commentService.updateComment(comment, postService.findById(comment.getPostId()));
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isCommentAuthor(authentication, #comment.id)")
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody CommentDTO comment, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return parseBindingResult(bindingResult);
        User user = userService.findById(comment.getAuthorId());

        if (user == null) {
            return new ResponseEntity<>(NO_USER_ERROR, HttpStatus.NOT_FOUND);
        }
        if (!commentService.deleteComment(comment)) return new ResponseEntity<>(NO_COMMENT_ERROR, HttpStatus.NOT_FOUND);
        return ResponseEntity.ok("Post has been deleted successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<?> getComments(
            @RequestParam(value = "postId") long postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentsPage = commentService.getCommentsByPostId(postId, pageable);

        List<CommentDTO> commentsDTO = commentsPage.getContent().stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentsDTO);
    }
}

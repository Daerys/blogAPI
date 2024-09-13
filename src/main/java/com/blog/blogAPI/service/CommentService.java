package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.Comment;
import com.blog.blogAPI.domain.Post;
import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.CommentDTO;
import com.blog.blogAPI.repository.CommentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.blog.blogAPI.utils.ExceptionMessagesUtils.NO_POST_ID_MESSAGE;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public boolean existById(Long id) {
        return findById(id) != null;
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public Page<Comment> getCommentsByPostId(long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    public CommentDTO createComment(User user, Post post, @Valid CommentDTO commentDTO) {
        if (post == null) return null;
        Comment comment = new Comment(commentDTO.getContent(), post, user);
        save(comment);
        return new CommentDTO(comment);
    }

    public CommentDTO updateComment(@Valid CommentDTO commentDTO, Post post) {
        Comment comment = findById(commentDTO.getId());
        comment.setContent(commentDTO.getContent());
        comment.setPost(post);
        save(comment);
        return new CommentDTO(comment);
    }

    public boolean deleteComment(CommentDTO comment) {
        try {
            if (!existById(comment.getId())) {
                return false;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(NO_POST_ID_MESSAGE);
        }

        delete(findById(comment.getId()));
        return true;
    }
}

package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.Comment;
import com.blog.blogAPI.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}

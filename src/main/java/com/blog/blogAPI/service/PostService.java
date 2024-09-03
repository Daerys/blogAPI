package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.Post;
import com.blog.blogAPI.dto.PostDTO;
import com.blog.blogAPI.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void save(Post post) {
        postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public boolean existById(Long id) {
        return findById(id) != null;
    }

    public List<Post> findByOwnerId(Long userId) {
        return postRepository.findAllByAuthorId(userId);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public boolean existsById(Long postId) {
        return findById(postId) != null;
    }
}

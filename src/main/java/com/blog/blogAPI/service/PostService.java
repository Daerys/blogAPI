package com.blog.blogAPI.service;

import com.blog.blogAPI.domain.Post;
import com.blog.blogAPI.domain.User;
import com.blog.blogAPI.dto.PostDTO;
import com.blog.blogAPI.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.blog.blogAPI.utils.ExceptionMessagesUtils.*;

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

    public PostDTO createPost(User user, @Valid PostDTO postDTO) {
        Post post = new Post(postDTO.getTitle(), postDTO.getContent(), user);
        save(post);
        return new PostDTO(post);
    }

    public PostDTO updatePost(User user, @Valid PostDTO postDTO) {
        Post post = findById(postDTO.getId());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(user);
        save(post);
        return new PostDTO(post);
    }

    public boolean deletePost(PostDTO post) {
        try {
            if (!existById(post.getId())) {
                return false;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(NO_POST_ID_MESSAGE);
        }

        delete(findById(post.getId()));
        return true;
    }
}

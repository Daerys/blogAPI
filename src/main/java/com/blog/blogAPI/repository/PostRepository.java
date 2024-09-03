package com.blog.blogAPI.repository;

import com.blog.blogAPI.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAuthorId(Long userId);
}

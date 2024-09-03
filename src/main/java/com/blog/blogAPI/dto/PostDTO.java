package com.blog.blogAPI.dto;

import com.blog.blogAPI.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PostDTO {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 10000, message = "Content should not exceed 10000 characters")
    private String content;

    @NotNull(message = "Owner is mandatory")
    private Long authorId;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorId = post.getAuthor().getId();
    }
}

package com.blog.blogAPI.dto;

import com.blog.blogAPI.domain.Comment;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDTO {
    private Long id;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 2000, message = "Content should not exceed 2000 characters")
    private String content;

    @NotNull(message = "Created date is mandatory")
    private LocalDateTime createdDate;

    @NotNull(message = "Post is mandatory")
    private Long postId;

    @JoinColumn(name = "user_id")
    @NotNull(message = "Author is mandatory")
    private Long authorId;

    public CommentDTO() {
        this.createdDate = LocalDateTime.now();
    }

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdDate = comment.getCreatedDate();
        this.postId = comment.getPost().getId();
        this.authorId = comment.getAuthor().getId();
    }
}

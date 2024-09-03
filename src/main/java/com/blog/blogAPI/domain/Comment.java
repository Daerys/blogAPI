package com.blog.blogAPI.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment implements HasAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String content;

    @Column
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    public Comment() {
        this.createdDate = LocalDateTime.now();
    }

    public Comment(String content, Post post, User author) {
        this.content = content;
        this.createdDate = LocalDateTime.now();
        this.post = post;
        this.author = author;
    }

}

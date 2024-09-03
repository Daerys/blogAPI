package com.blog.blogAPI.domain;

public interface HasAuthor {
    User getAuthor();
    void setAuthor(User user);
}

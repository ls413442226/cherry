package com.cherry.domain.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogArticle {

    private Long id;

    private Long authorId;

    private String title;

    private String content;

    private Integer status; // 0草稿 1发布

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

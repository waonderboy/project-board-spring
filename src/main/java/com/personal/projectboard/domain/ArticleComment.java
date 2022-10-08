package com.personal.projectboard.domain;

import java.time.LocalDateTime;

public class ArticleComment {
    private Long id;
    private String content;
    private Article article;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}

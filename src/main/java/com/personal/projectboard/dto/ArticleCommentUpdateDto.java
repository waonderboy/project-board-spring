package com.personal.projectboard.dto;

import java.time.LocalDateTime;

public record ArticleCommentUpdateDto(
        LocalDateTime modifiedAt,
        String modifiedBy,
        String content) {

    public static ArticleCommentUpdateDto of(LocalDateTime modifiedAt, String modifiedBy, String content) {
        return new ArticleCommentUpdateDto(modifiedAt, modifiedBy, content);
    }
}

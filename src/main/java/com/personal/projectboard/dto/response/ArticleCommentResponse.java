package com.personal.projectboard.dto.response;

import com.personal.projectboard.dto.ArticleCommentDto;

import java.time.LocalDateTime;

public record ArticleCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String nickname,
        String email,
        String userId
) {
    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String nickname, String email, String userId) {
        return new ArticleCommentResponse(id, content, createdAt, nickname, email, userId);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        return new ArticleCommentResponse(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().email(),
                dto.userAccountDto().userId()
        );
    }
}

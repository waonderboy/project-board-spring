package com.personal.projectboard.dto.response;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.dto.ArticleDto;
import com.personal.projectboard.dto.UserAccountDto;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String nickname,
        String email
) {
    public static ArticleResponse of(Long id, String title, String content, String hashtag, LocalDateTime createdAt, String nickname, String email) {
        return new ArticleResponse(id, title, content, hashtag, createdAt, nickname, email);
    }

    public static ArticleResponse from(ArticleDto dto) {
        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.createdAt(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().email());
    }

}

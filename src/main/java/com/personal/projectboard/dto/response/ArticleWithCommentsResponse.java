package com.personal.projectboard.dto.response;

import com.personal.projectboard.dto.ArticleCommentDto;
import com.personal.projectboard.dto.ArticleWithCommentsDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        Set<ArticleCommentResponse> articleCommentResponse,
        LocalDateTime createdAt,
        String nickname,
        String email
) {
    public static ArticleWithCommentsResponse of(Long id, String title, String content, String hashtag, Set<ArticleCommentResponse> articleCommentResponse, LocalDateTime createdAt, String nickname, String email) {
        return new ArticleWithCommentsResponse(id, title, content, hashtag, articleCommentResponse, createdAt, nickname, email);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.articleCommentDtoSet().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toSet()),
                dto.createdAt(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().email()
        );
    }
}

package com.personal.projectboard.dto;

import com.personal.projectboard.domain.Article;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
public record ArticleWithCommentsDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        Set<ArticleCommentDto> articleCommentDtoSet,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleWithCommentsDto of(Long id, UserAccountDto userAccountDto, String title, String Content, String hashtag, Set<ArticleCommentDto> articleCommentDtoSet, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleWithCommentsDto(id, userAccountDto, title, Content, hashtag, articleCommentDtoSet, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsDto from(Article entity) {
        return new ArticleWithCommentsDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getArticleComments().stream().
                        map(ArticleCommentDto::from)
                        .collect(Collectors.toSet()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}

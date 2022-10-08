package com.personal.projectboard.repository;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
}

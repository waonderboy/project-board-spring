package com.personal.projectboard.repository;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.dto.type.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryQuerydsl {
    Page<Article> findBySearchCond(SearchType type, String keyword, Pageable pageable);
}

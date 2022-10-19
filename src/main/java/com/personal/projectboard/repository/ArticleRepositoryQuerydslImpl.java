package com.personal.projectboard.repository;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.dto.contant.SearchType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.personal.projectboard.domain.QArticle.*;

public class ArticleRepositoryQuerydslImpl implements ArticleRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryQuerydslImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        return queryFactory
                .select(article.hashtag)
                .from(article)
                .distinct()
                .fetch();
    }

    @Override
    public Page<Article> findBySearchCond(SearchType searchType, String keyword, Pageable pageable) {
        List<Article> articles = queryFactory
                .select(article)
                .from(article)
                .where(
                        containsKeywordMatchingSearchType(searchType, keyword)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory
                .select(article.count())
                .from(article)
                .where(
                        containsKeywordMatchingSearchType(searchType, keyword)
                )
                ;

        return PageableExecutionUtils.getPage(articles, pageable, countQuery::fetchOne);
    }

    private BooleanExpression containsKeywordMatchingSearchType(SearchType searchType, String keyword) {
        String type = String.valueOf(searchType);
        PathBuilder<Article> article = new PathBuilder<>(Article.class, "article");
        //TODO: type없고 키워드만 있을 경우
        return type != null && keyword != null ? article.getString(type).contains(keyword) : null;
    }
}

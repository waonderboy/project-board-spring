package com.personal.projectboard.repository;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.dto.type.SearchType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.personal.projectboard.domain.QArticle.*;
import static org.springframework.util.StringUtils.*;

public class ArticleRepositoryQuerydslImpl implements ArticleRepositoryQuerydsl{

    private final JPAQueryFactory queryFactory;

    public ArticleRepositoryQuerydslImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
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

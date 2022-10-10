package com.personal.projectboard.repository;

import com.personal.projectboard.config.JpaConfig;
import com.personal.projectboard.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JPA 연결테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }


    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given


        // When
        List<Article> articles = articleRepository.findAll();
        long count = articles.stream().count();

        // Then
        assertThat(articles).isNotNull().hasSize(6);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // Given
        long previousCount = articleRepository.count();
        Article article = Article.of("new article", "new content", "#spring");
        // When
        Article savedArticle = articleRepository.save(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        Article savedArticle = articleRepository.saveAndFlush(Article.of("new article", "new content", "#spring"));

        Article findArticle = articleRepository.findById(savedArticle.getId()).orElseThrow();
        String changedHashtag = "#spring boot";
        findArticle.setHashtag(changedHashtag);

        // When
        Article updatedArticle = articleRepository.save(findArticle);
        articleRepository.saveAndFlush(findArticle);

        // Then
        assertThat(updatedArticle.getHashtag()).isEqualTo(changedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // Given
        Article article = Article.of("new article", "new content", "#spring");
        articleRepository.saveAndFlush(article);

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(6);
    }
}
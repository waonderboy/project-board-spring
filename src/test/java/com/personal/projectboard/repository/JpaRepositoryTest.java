package com.personal.projectboard.repository;

import com.personal.projectboard.config.JpaConfig;
import com.personal.projectboard.config.TestSecurityConfig;
import com.personal.projectboard.domain.Article;
import com.personal.projectboard.domain.UserAccount;
import com.personal.projectboard.dto.ArticleDto;
import com.personal.projectboard.dto.UserAccountDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JPA 연결테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository,
                             @Autowired UserAccountRepository userAccountRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }


    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given
        UserAccount user = createUser();
        userAccountRepository.save(user);
        // When
        List<Article> articles = articleRepository.findAll();
        long count = articles.stream().count();

        // Then
        assertThat(articles).isNotNull().hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // Given
        UserAccount user = createUser();
        userAccountRepository.save(user);
        long previousCount = articleRepository.count();
        Article article = Article.of(user, "new article", "new content", "#spring");
        // When
        Article savedArticle = articleRepository.save(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        UserAccount user = createUser();
        userAccountRepository.save(user);
        Article savedArticle = articleRepository.saveAndFlush(Article.of(user, "new Title", "new content", "#spring"));

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
        UserAccount user = createUser();
        userAccountRepository.save(user);
        Article article = Article.of(user,"new article", "new content", "#spring");
        articleRepository.saveAndFlush(article);

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(123);
    }

    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("kim");
        }
    }

    private UserAccountDto createUserDto() {
        return UserAccountDto.of("tkt2k", "adsf!@#", "tkt2k@naver.com", "wboy", "great");
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.of(createUserDto(), "Title", "good content", "#JAVA");
    }

    private UserAccount createUser() {
        return createUserDto().toEntity();
    }
}
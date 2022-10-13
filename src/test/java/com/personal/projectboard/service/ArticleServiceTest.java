package com.personal.projectboard.service;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.dto.ArticleDto;
import com.personal.projectboard.dto.ArticleUpdateDto;
import com.personal.projectboard.dto.type.SearchType;
import com.personal.projectboard.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.personal.projectboard.domain.QArticle.article;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService sut; //sut 테스트 대상 mock을 주입하는 대상
    @Mock
    private ArticleRepository articleRepository;

    /**
     * 정렬 페이징 소팅 가능
     */
    @DisplayName("게시글을 검색하면, 게시글 리스트 반환")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnArticleList(){
        // Given
        //SearchParams params = SearchParmas.of(SearchType.Title, "search keyword");

        // When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        // Then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 클릭하면, 게시글로 이동")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnArticle(){
        // Given

        // When
        ArticleDto article = sut.searchArticle(1L);

        // Then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 생성")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSaveArticle(){
        // Given
        ArticleDto dto = ArticleDto.of(
                LocalDateTime.now(), "kim", "Title", "Content", "#Java"
        );

        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.saveArticle(dto);

        // Then
        then(articleRepository).should().save(any(Article.class)); // save가 호출되었었는
    }

    @DisplayName("게시글의 아이디와 수정 정보를 입력하면 게시글을 수정한다")
    @Test
    void givenModifiedInfo_whenUpdatingArticle_thenUpdateArticle(){
        // Given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // When
        sut.updateArticle(1L, ArticleUpdateDto.of("Title", "Content", "#Java"));

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 아이디가 주어지면 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeleteArticle(){
        // Given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // When
        sut.deleteArticle(1L);

        // Then
        then(articleRepository).should().delete(any(Article.class));
    }

}
package com.personal.projectboard.service;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.domain.UserAccount;
import com.personal.projectboard.dto.ArticleDto;
import com.personal.projectboard.dto.UserAccountDto;
import com.personal.projectboard.dto.type.SearchType;
import com.personal.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks
    private ArticleService sut; //sut 테스트 대상 mock을 주입하는 대상
    @Mock
    private ArticleRepository articleRepository;

    @DisplayName("검색어로 게시글을 검색하면, 게시글 페이지를 반환한다. 검색어 없으면 전체페이지 조회")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnArticleList(){
        // Given
        PageRequest pageRequest = PageRequest.of(50,100);
        given(articleRepository.findAll(pageRequest)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageRequest);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageRequest);
    }

    @DisplayName("검색어 없이 게시글 해시태그를 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticlesViaHashtag_thenReturnEmptyPage(){
        // Given
        PageRequest pageRequest = PageRequest.of(50,100);

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageRequest);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageRequest));
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("해시태그로 게시글을 검색하면, 게시글  페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnArticlesPage(){
        // Given
        String hashtag = "#java";
        PageRequest pageRequest = PageRequest.of(50,100);
        given(articleRepository.findByHashtagContaining(hashtag, pageRequest)).willReturn(Page.empty(pageRequest));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageRequest);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageRequest));
        then(articleRepository).should().findByHashtagContaining(hashtag, pageRequest);
    }

    @DisplayName("해시태그를 조회하면, 유니크한 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnHashtags(){
        // Given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);

        // When
        List<String> hashtags = sut.getHashtags();

        // Then
        assertThat(hashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }

    @DisplayName("게시글을 검색하면, 게시글 리스트 반환")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnArticleList1(){
        // Given
        PageRequest pageRequest = PageRequest.of(50,100);
        SearchType searchType = SearchType.TITLE;
        String keyword = "keyword";
        given(articleRepository.findByTitleContaining(keyword,pageRequest)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articleDto = sut.searchArticles(searchType, keyword, pageRequest);

        // Then
        assertThat(articleDto).isEmpty();
        then(articleRepository).should().findByTitleContaining(keyword, pageRequest);
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnArticle(){
        // Given
        long articleId = 1L;
        Article article = createArticle(articleId);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        sut.getArticle(articleId);

        // Then
        assertThat(article).hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());

        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다")
    @Test
    void givenArticleId_whenSearchingArticle_thenThrowsExceptions(){
        // Given
        Long articleId = 0L;
        Article article = createArticle(1L);
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSaveArticle(){
        // Given
        ArticleDto articleDto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle(1L));

        // When
        sut.saveArticle(articleDto);

        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 아이디와 수정 정보를 입력하면 게시글을 수정한다")
    @Test
    void givenModifiedInfo_whenUpdatingArticle_thenUpdateArticle(){
        // Given
        Article article = createArticle(1L);
        ArticleDto dto = ArticleDto.from(article);
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        // When
        sut.updateArticle(dto);

        // Then
        assertThat(article).hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());

        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정정보를 입력하면 경고로그를 찍고 아무것도 하지않는다.")
    void givenNoExistentArticleInfo_whenUpdatingArticle_thenLogWarningAndDoNotAnything(){
        // Given
        ArticleDto articleDto = ArticleDto.of(createUserDto(),"change", "change","#change");
        given(articleRepository.getReferenceById(articleDto.id())).willThrow((EntityNotFoundException.class));

        // When
        sut.updateArticle(articleDto);

        //
        then(articleRepository).should().getReferenceById(articleDto.id());


    }

    @DisplayName("게시글의 아이디가 입력하면, 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeleteArticle(){
        // Given
        long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        // When
        sut.deleteArticle(1L);

        // Then
        then(articleRepository).should().deleteById(articleId);
    }

    private UserAccountDto createUserDto() {
        return UserAccountDto.from(creatUser());
    }

    private ArticleDto createArticleDto() {
        return ArticleDto.from(createArticle(1L));
    }

    private UserAccount creatUser() {
        return UserAccount.of("tkt2k", "adsf!@#", "tkt2k@naver.com", "wboy", "great");
    }

    private Article createArticle(Long id) {
        Article article = Article.of(
                creatUser(),
                "title",
                "content",
                "#java"
        );

        ReflectionTestUtils.setField(article, "id", id);

        return article;
    }

}
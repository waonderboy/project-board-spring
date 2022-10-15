package com.personal.projectboard.service;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.domain.ArticleComment;
import com.personal.projectboard.dto.ArticleCommentDto;
import com.personal.projectboard.dto.UserAccountDto;
import com.personal.projectboard.repository.ArticleCommentRepository;
import com.personal.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스로직 - 게시글 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService sut; //sut 테스트 대상 mock을 주입하는 대상
    @Mock
    private ArticleCommentRepository articleCommentRepository;
    @Mock
    private ArticleRepository articleRepository;

    @DisplayName("게시글을 ID로 조회하면 주어지면, 댓글 리스트 반환")
    @Test
    void givenArticleId_whenSearchingComments_thenReturnArticleComments(){
        // Given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.ofNullable(null));

        // When
        List<ArticleCommentDto> articleComments = sut.searchArticleComments(articleId);

        // Then
        assertThat(articleComments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 생성")
    @Test
    void givenArticleCommentInfo_whenSavingComment_thenSaveComment(){
        // Given

        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        sut.saveArticleComments(createArticleCommentDto());

        // Then
        then(articleRepository).should().save(any(Article.class)); // save가 호출되었었는
    }


    @DisplayName("게시글의 아이디와 수정 정보를 입력하면 게시글을 수정한다")
    @Test
    void givenModifiedInfo_whenUpdatingComment_thenUpdateComment(){
        // Given
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // When
        sut.updateArticleComment(1L, createArticleCommentDto());

        // Then
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("게시글의 아이디가 주어지면 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingComment_thenDeleteComment(){
        // Given
        willDoNothing().given(articleCommentRepository).delete(any(ArticleComment.class));

        // When
        sut.deleteArticleComment(1L);

        // Then
        then(articleRepository).should().delete(any(Article.class));
    }

    private UserAccountDto createUserDto() {
        return UserAccountDto.of("tkt2k", "adsf!@#", "tkt2k@naver.com", "wboy", "great");
    }

    private ArticleCommentDto createArticleCommentDto() {
        return ArticleCommentDto.of(
                1L, createUserDto(),"content"
        );
    }
}
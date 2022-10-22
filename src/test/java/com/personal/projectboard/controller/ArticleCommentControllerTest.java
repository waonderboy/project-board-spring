package com.personal.projectboard.controller;

import com.personal.projectboard.config.SecurityConfig;
import com.personal.projectboard.config.TestSecurityConfig;
import com.personal.projectboard.dto.ArticleCommentDto;
import com.personal.projectboard.dto.request.ArticleCommentRequest;
import com.personal.projectboard.service.ArticleCommentService;
import com.personal.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View 컨트롤러 - 댓글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleCommentController.class)
class ArticleCommentControllerTest {

    private final MockMvc mockMvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private ArticleCommentService articleCommentService;


    public ArticleCommentControllerTest(@Autowired MockMvc mockMvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mockMvc = mockMvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "testname", userDetailsServiceBeanName = "userDetailsService" , setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 댓글 등록 - 정상 호출")
    @Test
    void givenArticleCommentInfo_whenRequesting_thenSavesNewArticleComment() throws Exception {
        // Given
        long articleId = 1L;
        ArticleCommentRequest request =  ArticleCommentRequest.of(articleId, "test comment");
        willDoNothing().given(articleCommentService).saveArticleComment(ArgumentMatchers.any(ArticleCommentDto.class));

        // When & Then
        mockMvc.perform(
                    post("/comments/new")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .content(formDataEncoder.encode(request))
                            .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleCommentService).should().saveArticleComment(ArgumentMatchers.any(ArticleCommentDto.class));

    }
    @WithUserDetails(value = "testname", userDetailsServiceBeanName = "userDetailsService" , setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 댓글 삭제 - 정상 호출")
    @Test
    void givenArticleCommentIdToDelete_whenRequesting_thenDeletesArticleComment() throws Exception {
        // Given
        long articleId = 1L;
        long articleCommentId = 1L;
        String userId = "kim";
        willDoNothing().given(articleCommentService).deleteArticleComment(articleCommentId, userId);

        // When & Then
        mockMvc.perform(
                        post("/comments/" + articleCommentId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("articleId", articleId)))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles/" + articleId))
                .andExpect(redirectedUrl("/articles/" + articleId));
        then(articleCommentService).should().deleteArticleComment(articleCommentId, userId);
    }

}
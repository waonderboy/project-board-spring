package com.personal.projectboard.controller;

import com.personal.projectboard.config.SecurityConfig;
import com.personal.projectboard.config.TestSecurityConfig;
import com.personal.projectboard.domain.Article;
import com.personal.projectboard.domain.UserAccount;
import com.personal.projectboard.dto.ArticleDto;
import com.personal.projectboard.dto.ArticleWithCommentsDto;
import com.personal.projectboard.dto.UserAccountDto;
import com.personal.projectboard.dto.contant.FormStatus;
import com.personal.projectboard.dto.request.ArticleRequest;
import com.personal.projectboard.dto.response.ArticleResponse;
import com.personal.projectboard.dto.contant.SearchType;
import com.personal.projectboard.service.ArticleService;
import com.personal.projectboard.service.PaginationService;
import com.personal.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import({TestSecurityConfig.class,FormDataEncoder.class})
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private PaginationService paginationService;


    private FormDataEncoder formDataEncoder;

    public ArticleControllerTest(@Autowired MockMvc mockMvc, @Autowired FormDataEncoder formDataEncoder) {
        this.mockMvc = mockMvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class)))
                .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When
        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBarNumbers"));
        // Then
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 검색 기능")
    @Test
    void givenSearchKeyword_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String keyword = "Lorem";

        given(articleService.searchArticles(eq(searchType), eq(keyword), any(Pageable.class)))
                .willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When
        mockMvc.perform(get("/articles")
                        .queryParam("searchType", String.valueOf(searchType))
                        .queryParam("keyword", keyword)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));
        // Then
        then(articleService).should().searchArticles(eq(searchType), eq(keyword), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬기능")
    @Test
    void givenPagingAndSortingParam_whenRequestingArticlesPages_thenReturnsArticlesPages() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mockMvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());

    }

    @DisplayName("[view][GET] 게시글 페이지 - 인증이 없을때 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequestingArticlePage_thenReturnsLoginPage() throws Exception {
        // Given
        long articleId = 1L;

        // When
        mockMvc.perform(get("/articles/" + articleId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Then
        then(articleService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출, 인증된 사용자")
    @Test
    void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        // Given
        long articleId = 1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(articleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(1L);

        // When
        mockMvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/details"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));

        // Then
        then(articleService).should().getArticleWithComments(articleWithCommentsDto().id());
    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 작성 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleForm_thenReturnsArticleForm() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(get("/articles/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/article-form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value = "testname", userDetailsServiceBeanName = "userDetailsService" , setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        // Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        // When & Then
        mockMvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }
    @WithUserDetails(value = "testname", userDetailsServiceBeanName = "userDetailsService" , setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 게시글 업데이트 페이지 요청")
    @Test
    void givenNothing_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleDto dto = createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);

        // When
        mockMvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/article-form"))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));

        // Then
        then(articleService).should().getArticle(articleId);
    }


    @Disabled
    @DisplayName("[view][POST] 게시글 업데이트 요청 - NEW")
    @Test
    void givenNothing_whenUpdatingArticle_thenRedirectingArticlePage() throws Exception {
        // Given
        long articleId = 1L;
        ArticleRequest articleRequest = ArticleRequest.of("title", "content", "#java");
        ArticleDto articleDto = articleRequest.toDto(createUserAccountDto());

        // When
        mockMvc.perform(post("/articles/"+articleId+"/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(articleRequest)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("articles/details"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));

        // Then
    }

    @WithUserDetails(value = "testname", userDetailsServiceBeanName = "userDetailsService" , setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][GET] 게시글 삭제 요청")
    @Test
    void givenNothing_whenDeletingArticle_thenReturnsArticleForm() throws Exception {
        // Given
        long articleId = 1L;
        String userId = "kim";
        willDoNothing().given(articleService).deleteArticle(articleId, userId);

        // When & Then
        mockMvc.perform(
                        post("/articles/" + articleId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));

        then(articleService).should().deleteArticle(articleId, userId);

    }


    @DisplayName("[view][GET] 해시태그 전용 페이지 - 입력값 없음")
    @Test
    void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        // Given
        List<String> hashtagList = List.of("#java", "#spring");
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        given(articleService.getHashtags()).willReturn(hashtagList);
        // When
        mockMvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags" , hashtagList))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG))
        ;
        // Then
        then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class));
        then(articleService).should().getHashtags();
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 해시태그 전용 페이지 - 입력값 존재")
    @Test
    void givenHashtag_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        // Given
        List<String> hashtagList = List.of("#java", "#spring");
        String hashtag = "#java";
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        given(articleService.getHashtags()).willReturn(hashtagList);

        // When
        mockMvc.perform(get("/articles/search-hashtag")
                        .queryParam("searchValue", hashtag)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"))
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attribute("hashtags" , hashtagList))
                .andExpect(model().attributeExists("paginationBarNumbers"))
                .andExpect(model().attribute("searchType", SearchType.HASHTAG));
        // Then
        then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
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



    private ArticleWithCommentsDto articleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                "title",
                "content",
                "hashtag",
                Set.of(),
                LocalDateTime.now(),
                "waonderboy",
                LocalDateTime.now(),
                "waonderboy"
        );
    }

    private UserAccountDto createUserAccountDto () {
        return UserAccountDto.of(
                "wonon",
                "adfas!@#!",
                "kkk@gmail.com",
                "freesia",
                "vip",
                LocalDateTime.now(),
                "waonderboy",
                LocalDateTime.now(),
                "waonderboy"
        );
    }
}
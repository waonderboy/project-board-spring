package com.personal.projectboard.controller;


import com.personal.projectboard.dto.ArticleDto;
import com.personal.projectboard.dto.UserAccountDto;
import com.personal.projectboard.dto.contant.FormStatus;
import com.personal.projectboard.dto.request.ArticleRequest;
import com.personal.projectboard.dto.response.ArticleCommentResponse;
import com.personal.projectboard.dto.response.ArticleResponse;
import com.personal.projectboard.dto.response.ArticleWithCommentsResponse;
import com.personal.projectboard.dto.contant.SearchType;
import com.personal.projectboard.service.ArticleService;
import com.personal.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map){

        Page<ArticleResponse> articlesPage = articleService.searchArticles(searchType, keyword, pageable)
                .map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articlesPage.getTotalPages());

        map.addAttribute("articles", articlesPage);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map){

        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        Set<ArticleCommentResponse> comments = article.articleCommentResponse();

        map.addAttribute("article", article);
        map.addAttribute("articleComments", comments);
        map.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/details";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);
        return "articles/article-form";
    }

    @PostMapping ("/form")
    public String postNewArticle(ArticleRequest articleRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.saveArticle(articleRequest.toDto(UserAccountDto.of(
                "uno", "asdf1234", "uno@mail.com", "Uno", "memo", null, null, null, null
        )));

        return "redirect:/articles";
    }

    /*
    @PostMapping("/form")
    public String registerArticle(ArticleRequest articleRequest, ModelMap map){
        // TODO :: 인증정보 필요
        // TODO :: 유저서비스부터 만들었어야 정상조회됨, 저장 후 조회시 유저가 persist 되어있지 않기 때문에
        //  ArticleResponse 생성시 userAccountDto 부분에서 nullPointEx 발생
        //  -> 일단 리다이렉션을 전체 게시글로 설정해놓고 추후 변경 필요
        ArticleDto articleDto = articleRequest.toDto(UserAccountDto.of("uno", "asdf1234", "uno@mail.com", "Uno", "memo", null, null, null, null
        ));
        long saveArticleId = articleService.saveArticle(articleDto);

        ArticleResponse articleResponse = ArticleResponse.from(articleService.getArticle(saveArticleId));

        map.addAttribute("article", articleResponse);

        return "redirect:/articles/details";
    }
    */

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Integer articleId, ArticleRequest articleRequest, ModelMap map) {
        // TODO: 인증 정보를 넣어줘야 한다.
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));
        map.addAttribute("formStatus", FormStatus.UPDATE);
        map.addAttribute("article", article);

        return "articles/article-form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Integer articleId, ArticleRequest articleRequest, ModelMap map) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.updateArticle(articleId, articleRequest.toDto(UserAccountDto.of(
                "uno", "asdf1234", "uno@mail.com", "Uno", "memo", null, null, null, null
        )));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(@PathVariable Integer articleId) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.deleteArticle(articleId);

        return "redirect:/articles";
    }

    @GetMapping("/search-hashtag")
    public String searchHashtag(
            @RequestParam(name = "searchValue", required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articlesPage = articleService.searchArticlesViaHashtag(keyword, pageable)
                .map(ArticleResponse::from);

        List<String> hashtags = articleService.getHashtags();
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articlesPage.getTotalPages());


        map.addAttribute("articles", articlesPage);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }



}

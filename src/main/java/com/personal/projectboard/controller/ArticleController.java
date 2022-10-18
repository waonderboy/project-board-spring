package com.personal.projectboard.controller;


import com.personal.projectboard.dto.response.ArticleCommentResponse;
import com.personal.projectboard.dto.response.ArticleResponse;
import com.personal.projectboard.dto.response.ArticleWithCommentsResponse;
import com.personal.projectboard.dto.type.SearchType;
import com.personal.projectboard.service.ArticleService;
import com.personal.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
        Set<ArticleCommentResponse> comments = article.articleCommentResponse();

        map.addAttribute("article", article);
        map.addAttribute("articleComments", comments);
        map.addAttribute("totalCount", articleService.getArticleCount());

        return "articles/details";
    }



}

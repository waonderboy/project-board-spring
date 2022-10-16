package com.personal.projectboard.service;


import com.personal.projectboard.dto.ArticleCommentDto;
import com.personal.projectboard.repository.ArticleCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return List.of();
    }

    public void saveArticleComments(ArticleCommentDto dto) {
        
    }


    public void updateArticleComment(long articleCommentId, ArticleCommentDto dto) {
    }

    public void deleteArticleComment(long articleCommentId) {

    }
}

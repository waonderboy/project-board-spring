package com.personal.projectboard.repository;

import com.personal.projectboard.domain.Article;
import com.personal.projectboard.domain.UserAccount;
import com.personal.projectboard.dto.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserAccountRepository userAccountRepository;


    @Test
    void saveTest() {
        UserAccount userAccount = userAccountRepository.save(creatUser());

        Article article1 = articleRepository.save(createArticle(userAccount));

        System.out.println("save.getId() = " + article1.getId());

    }
    private UserAccount creatUser() {
        return UserAccount.of("tkt2k", "adsf!@#", "tkt2k@naver.com", "wboy", "great");
    }

    private Article createArticle(UserAccount userAccount) {
        Article article = Article.of(
                userAccount,
                "title",
                "content",
                "#java"
        );


        return article;
    }
}

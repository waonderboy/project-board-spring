package com.personal.projectboard.config;


import com.personal.projectboard.domain.UserAccount;
import com.personal.projectboard.repository.UserAccountRepository;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean
    private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "kim",
                "adsfs!@#",
                "aaa@naver.com",
                "nick",
                "ddd"
        )));
    }
}

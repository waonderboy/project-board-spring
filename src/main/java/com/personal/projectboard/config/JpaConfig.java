package com.personal.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String>auditorAware() {
        return () -> Optional.of("kim"); // TODO : 스프 시큐리티로 인증기능을 붙힐때, 수정 필요
    }
}

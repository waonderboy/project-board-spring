package com.personal.projectboard.controller;

import com.personal.projectboard.config.SecurityConfig;
import com.personal.projectboard.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void givenNothing_whenRequestingRootPage_thenRedirectsToArticlePage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/"))
                .andExpect(status().isOk());


    }

}
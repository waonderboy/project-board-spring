package com.personal.projectboard.dto.type;

import lombok.Getter;

public enum SearchType {
    TITLE("제목"),
    CONTENT("본문"),
    ID("아이디"),
    NICKNAME("닉네임"),
    HASHTAG("해시태그");

    @Getter private String description;

    SearchType(String description) {
        this.description = description;
    }
}

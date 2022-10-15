package com.personal.projectboard.dto;

//@Entity
//public class UserAccount extends AuditingFields {
//    @Id
//    @Column(length = 50)
//    private String userId;
//
//    @Setter
//    @Column(nullable = false) private String userPassword;
//
//    @Setter @Column(length = 100) private String email;
//    @Setter @Column(length = 100) private String nickname;
//    @Setter private String memo;

import com.personal.projectboard.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDto(
        String userId,
        String userPassword,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo) {
        return new UserAccountDto(userId, userPassword, email, nickname, memo, null, null, null, null);
    }

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(userId, userPassword, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserAccountDto from(UserAccount entity){
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy());
    }

    public UserAccount toEntity(){
        return UserAccount.of(
                userId,
                userPassword,
                email,
                nickname,
                memo
        );
    }
}

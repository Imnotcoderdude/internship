package com.minimallifestyleproject.intership.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Getter
    @RequiredArgsConstructor
    public enum Role {
        ROLE_USER,
        ROLE_ADMIN;
    }

    @Builder
    public UserEntity(String username, String password, String nickname ,Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}

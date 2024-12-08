package com.minimallifestyleproject.intership.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {

    private String username;
    private String nickname;
    private List<Authority> authorities;

    public UserResponseDto(String username, String nickname, List<Authority> authorities) {
        this.username = username;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    @Data
    public static class Authority {
        private String authorityName;

        public Authority(String authorityName) {
            this.authorityName = authorityName;
        }
    }
}

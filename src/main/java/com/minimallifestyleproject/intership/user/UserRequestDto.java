package com.minimallifestyleproject.intership.user;

import lombok.Data;

@Data
public class UserRequestDto {

    private String username;
    private String password;
    private String nickname;
}
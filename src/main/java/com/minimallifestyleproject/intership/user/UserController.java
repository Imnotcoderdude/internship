package com.minimallifestyleproject.intership.user;

import com.minimallifestyleproject.intership.user.dto.LoginRequestDto;
import com.minimallifestyleproject.intership.user.dto.LoginResponseDto;
import com.minimallifestyleproject.intership.user.dto.UserRequestDto;
import com.minimallifestyleproject.intership.user.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 API")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.signup(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/sign")
    @Operation(summary = "로그인")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}

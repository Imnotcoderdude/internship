package com.minimallifestyleproject.intership.user;

import com.minimallifestyleproject.intership.security.CustomUserDetails;
import com.minimallifestyleproject.intership.security.JwtService;
import com.minimallifestyleproject.intership.user.dto.LoginRequestDto;
import com.minimallifestyleproject.intership.user.dto.LoginResponseDto;
import com.minimallifestyleproject.intership.user.dto.UserRequestDto;
import com.minimallifestyleproject.intership.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public UserResponseDto signup(UserRequestDto requestDto) {

        Objects.requireNonNull(requestDto, "회원가입 - UserRequestDto Null 값 할당됨");

        UserEntity user = UserEntity.builder()
                .username(requestException(requestDto.getUsername(), "회원가입" ,"username"))
                .nickname(requestException(requestDto.getNickname(), "회원가입" ,"nickname"))
                .password(passwordEncoder.encode(requestException(requestDto.getPassword(), "회원가입" ,"password")))
                .role(UserEntity.Role.ROLE_USER)
                .build();

        userRepository.save(user);

        UserResponseDto.Authority authority = new UserResponseDto.Authority(user.getRole().name());

        return new UserResponseDto(
                user.getUsername(),
                user.getNickname(),
                List.of(authority)
        );
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        Objects.requireNonNull(requestDto, "LoginRequestDto must not be null.");

        String username = requestException(requestDto.getUsername(), "로그인" ,"username");
        String password = requestException(requestDto.getPassword(), "로그인" ,"password");

        log.info("로그인 시작");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        null
                )
        );

        log.info("CustomUserDetails 객체 생성");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserEntity user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponseDto(accessToken);
    }

//////////////////////////// TOOL BOX ////////////////////////////////I
    private String requestException(String dto, String value ,String name) {
        return Objects.requireNonNull(dto, value+ " - " + name + " Null 값 할당됨");
    }

    //
//    public LoginResponseDto login(LoginRequestDto requestDto) {
//        log.info("로그인 시작");
//        UserEntity user = userRepository.findByUsername(requestDto.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
//        }
//
//        log.info("CustomUserDetails 객체 생성");
//
//        String accessToken = jwtService.generateAccessToken(user);
//        String refreshToken = jwtService.generateRefreshToken(user);
//
//        user.updateRefreshToken(refreshToken);
//        userRepository.save(user);
//
//        return new LoginResponseDto(accessToken, refreshToken);
//    }

}

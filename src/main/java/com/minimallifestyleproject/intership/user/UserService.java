package com.minimallifestyleproject.intership.user;

import com.minimallifestyleproject.intership.security.CustomUserDetails;
import com.minimallifestyleproject.intership.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public UserResponseDto signup(UserRequestDto requestDto) {

        UserEntity user = UserEntity.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
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
        log.info("로그인 시작");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword(),
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

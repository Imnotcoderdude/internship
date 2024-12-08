package com.minimallifestyleproject.intership.security;

import com.minimallifestyleproject.intership.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "you-you-you-you-you-you-you-super-equlim");
        ReflectionTestUtils.setField(jwtService, "accessExpireTime", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpireTime", 7200000L);
        jwtService.init(); // @PostConstruct 호출

        testUser = createdTestUser();
    }

    private UserEntity createdTestUser() {
        return UserEntity.builder()
                .username("testUser")
                .role(UserEntity.Role.ROLE_USER)
                .build();
    }

    @Test
    void generateAccessToken() {
        String accessToken = jwtService.generateAccessToken(testUser);

        assertNotNull(accessToken);
        System.out.println("엑세스 토큰 생성 : " + accessToken);
    }

    @Test
    void ValidateToken() {
        String token = jwtService.generateAccessToken(testUser);

        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void generateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testUser);

        assertNotNull(refreshToken);
        System.out.println("리프레쉬 토큰 생성: " + refreshToken);
    }

    @Test
    void getUsername() {
        String token = jwtService.generateAccessToken(testUser);
        String extractedUsername = jwtService.getUsername(token);

        assertEquals(testUser.getUsername(), extractedUsername);
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "accessExpireTime", 1000L); // 1초
        jwtService.init();

        String token = jwtService.generateAccessToken(testUser);

        Thread.sleep(1500);

        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void validateInvalidToken() {
        String invalidToken = "invalid_invalid_invalid_invalid_invalid_invalid";
        assertFalse(jwtService.validateToken(invalidToken));
    }
}
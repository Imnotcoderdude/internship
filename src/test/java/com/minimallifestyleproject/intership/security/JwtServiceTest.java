package com.minimallifestyleproject.intership.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.secretKey = "key-key-key-key-key-key-key-key-key"; // 테스트용 Secret Key
        jwtService.accessExpireTime = 1000 * 60 * 10; // 10분
        jwtService.refreshExpireTime = 1000 * 60 * 60 * 24; // 24시간
        jwtService.init();
    }

    @Test
    void createToken() {
        String username = "testUser";
        boolean isAccessToken = true;

        String token = jwtService.CreateToken(username, Collections.singletonList("ROLE_USER"), isAccessToken);

        assertNotNull(token);
        assertTrue((jwtService.validateToken(token)));
    }

    @Test
    void getUsername() {
        String token = jwtService.CreateToken("testUser", Collections.singletonList("ROLE_USER"), true);

        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateToken() {
        String invalidToken = "invalid_token";

        assertFalse(jwtService.validateToken(invalidToken));
    }

    @Test
    void isTokenExpired() throws InterruptedException {
        jwtService.accessExpireTime = 1;
        jwtService.init();
        String token = jwtService.CreateToken("testUser", Collections.singletonList("ROLE_USER"), true);

        Thread.sleep(5);

        boolean isExpired = jwtService.isTokenExpired(token);

        assertTrue(isExpired);
    }

    @Test
    void extractAllClaims() {
        String username = "testUser";
        String token = jwtService.CreateToken(username, Collections.singletonList("ROLE_USER"), true);

        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
    }
}
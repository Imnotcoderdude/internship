package com.minimallifestyleproject.intership.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_유효토큰_사용자인증() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        String validToken = "Bearer valid_token";
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("testUser");
        when(jwtService.validateToken("valid_token")).thenReturn(true);
        when(jwtService.extractAllClaims("valid_token")).thenReturn(claims);

        request.addHeader(HttpHeaders.AUTHORIZATION, validToken);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNotNull(securityContext.getAuthentication());
        assertEquals("testUser", securityContext.getAuthentication().getPrincipal());

    }

    @Test
    void doFilterInternal_유효하지않은토큰_사용자인증불가() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        String invalidToken = "Bearer invalid_token";
        when(jwtService.validateToken("invalid_token")).thenReturn(false);

        request.addHeader(HttpHeaders.AUTHORIZATION, invalidToken);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

    }

}
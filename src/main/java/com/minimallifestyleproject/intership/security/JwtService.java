package com.minimallifestyleproject.intership.security;

import com.minimallifestyleproject.intership.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtService {

    @Value("${key.SecretKey}")
    String secretKey;

    @Value("${token.access.time}")
    Long accessExpireTime;

    @Value("${token.refresh.time}")
    Long refreshExpireTime;

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 엑세스 토큰
    public String generateAccessToken(UserEntity user) {

        log.info("generateAccessToken 메서드 실행");
        return createToken(user.getUsername(), user.getRole(), accessExpireTime);
    }

    // 리프레쉬 토큰
    public String generateRefreshToken(UserEntity user) {

        log.info("generateRefreshToken 메서드 실행");
        return createToken(user.getUsername(), user.getRole(), refreshExpireTime);
    }

    public String createToken(String username, UserEntity.Role role, Long expireTime) {
        log.info("토큰 발행 시작");
        Date now = new Date();
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTime))
                .signWith(key,signatureAlgorithm)
                .compact();

        return token;
    }

    // 토큰에서 사용자 정보 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰: {}", e.getMessage());
            return false;
        }
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true; // 만료된 것으로 간주
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

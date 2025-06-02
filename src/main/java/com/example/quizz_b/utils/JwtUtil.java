package com.example.quizz_b.utils;

import com.example.quizz_b.model.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {
    // 取得環境變數
    @Value("${JWT_KEY}")
    private String SECRET_KEY;

    private Key key;

    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hr

    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 產生 Token
    public String generateToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDto.getId());
        claims.put("username", userDto.getUsername());
        claims.put("email", userDto.getEmail());
        claims.put("status", userDto.getStatus());
        claims.put("role", userDto.getRole());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userDto.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .and()
                .signWith(getKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    // 驗證 Token 是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) getKey()).build().parseSignedClaims(token).getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 從 Token 中取出 email
    public String extractEmail(String token) {
        Claims claims =  Jwts.parser().verifyWith((SecretKey) getKey()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
}

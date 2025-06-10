package com.example.quizz_b.utils;

import com.example.quizz_b.model.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


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

    // 解析並回傳 Token 中的所有 Claims（內容資料）
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) getKey()).build().parseSignedClaims(token).getPayload();
    }

    // 從 Token 中提取指定欄位（Claim）
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Token 中提取過期時間
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 檢查是否過期 (不驗證簽名正確性)
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /* 驗證 Token 是否有效
     * @param token JWT 字串
     * @param checkExpiration 是否驗證過期時間
     * @return true 表示有效；false 表示無效或過期
     */
    public boolean validateToken(String token, boolean checkExpiration) {
        try {
            if (checkExpiration) {
                return !isTokenExpired(token);
            } else {
                extractAllClaims(token); // 驗證簽章是否正確
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // 簡化方法：只驗證簽章合法性
    public boolean validateToken(String token) {
        try {
            isTokenExpired(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 從 Token 中取出 email
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 從 Token 中取出 userId
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> Long.parseLong(claims.get("id").toString()));
    }

    // 從 cookie 中拿到 token
    public String extractTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        throw new RuntimeException("JWT token 未提供");
    }
}

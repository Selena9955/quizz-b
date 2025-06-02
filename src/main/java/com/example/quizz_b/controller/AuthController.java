package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.UserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.UserService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(@RequestParam String email) {
        boolean available = !userService.isEmailRegistered(email);
        Map<String, Boolean> result = Map.of("available", available);

        return ResponseEntity.ok(ApiResponse.success("查詢成功", result));
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUsername(@RequestParam String username) {
        boolean available = !userService.isUsernameTaken(username);
        Map<String, Boolean> result = Map.of("available", available);

        return ResponseEntity.ok(ApiResponse.success("查詢成功", result));
    }


    // 註冊 http://localhost:8081/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        try {
            userService.createUser(username, password, email);
            return ResponseEntity.ok(ApiResponse.success("註冊成功", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    // 登入 http://localhost:8081/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse< Map<String, Object>>> login( @RequestBody Map<String, String> body, HttpServletResponse response){
        try {
            String email = body.get("email");
            String password = body.get("password");
            Map<String, Object> result = userService.login(email, password);

            // 取出 token，設 Cookie
            String token = (String) result.get("token");
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // https 本地測試用 false，正式環境改 true
                    .path("/") // 網域範圍為全部
                    .maxAge(24 * 60 * 60) // Cookie 的存活時間 - 24hr
                    .sameSite("Lax") // 防止跨站請求偽造（CSRF）的一種設定
                    .build(); // 完成，建立 Cookie
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 回傳時不包含 token，只回 user
            result.remove("token");

            return ResponseEntity.ok(ApiResponse.success("登入成功", result));
        }catch (RuntimeException ex){
            return ResponseEntity.badRequest().body(ApiResponse.error(401,"登入失敗:"+ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        // 清除 cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false) // 本地測試 false，正式環境 true
                .path("/")
                .maxAge(0) // 立即失效
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(ApiResponse.success("已登出", null));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("jwt")) {
                    String jwt = c.getValue();

                    try {
                        // 解析 JWT 拿到 email
                        String email = jwtUtil.extractEmail(jwt);
                        UserDto userDto = userService.findByemail(email);

                        return ResponseEntity.ok(Map.of("user", userDto));
                    } catch (Exception e) {
                        System.out.println("解析 email 失敗: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("message", "JWT 無效或過期"));

                    }
                }
            }
        }

        // 沒有 cookie 或沒有 jwt
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "未登入"));
    }


}

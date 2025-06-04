package com.example.quizz_b.controller;

import com.example.quizz_b.constant.enums.UserStatus;
import com.example.quizz_b.model.dto.RegisterRequestDto;
import com.example.quizz_b.model.dto.UserDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class AuthController {
    @Autowired
    private UserService userService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
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
    public ResponseEntity<ApiResponse<Map<String,String>>> register(@RequestBody RegisterRequestDto request) {
        try {
            // 用 RegisterRequestDto 檢查是格式
            userService.createUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getEmail()
            );
            Map<String, String> data = Map.of("email", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("註冊成功", data));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse< Map<String, Object>>> login( @RequestBody Map<String, String> body, HttpServletResponse response){
        try {
            String email = body.get("email");
            String password = body.get("password");
            Map<String, Object> result = userService.login(email, password);

            String token = (String) result.get("token");
            UserDto user = (UserDto) result.get("user");


            boolean isVerified = user.getStatus() == UserStatus.VERIFIED;

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("isVerified", isVerified);
            responseData.put("email", user.getEmail());

            // 設 Cookie
            if (isVerified) {
                ResponseCookie cookie = ResponseCookie.from("jwt", token)
                        .httpOnly(true)
                        .secure(false) // https 本地測試用 false，正式環境改 true
                        .path("/") // 網域範圍為全部
                        .maxAge(24 * 60 * 60) // Cookie 的存活時間 - 24hr
                        .sameSite("Lax") // 防止跨站請求偽造（CSRF）的一種設定
                        .build(); // 完成，建立 Cookie
                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                // 有驗證才提供user資料
                responseData.put("user", user);
            }
            return ResponseEntity.ok(ApiResponse.success("登入成功", responseData));
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
                        UserDto userDto = userService.findByEmail(email);

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

    @PostMapping("/send-verify-code")
    public ResponseEntity<ApiResponse<Void>> sendVerifyCode(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            userService.sendVerificationCode(email);
            return ResponseEntity.ok(ApiResponse.success("驗證碼已發送，請勿關閉本視窗", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
        }
    }

    @PostMapping("/check-verify-code")
    public ResponseEntity<ApiResponse<Void>> checkVerifyCode(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String code = body.get("code");
            userService.checkVerificationCode(email,code);
            return ResponseEntity.ok(ApiResponse.success("email驗證成功", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
        }
    }

}

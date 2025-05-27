package com.example.quizz_b.controller;

import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class AuthController {
    @Autowired
    private UserService userService;

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
}

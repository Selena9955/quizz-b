package com.example.quizz_b.controller;

import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}

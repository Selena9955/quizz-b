package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.ProfileDto;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.UserService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {
        @Autowired
        private UserService userService;

        @Autowired
        private JwtUtil jwtUtil;

        @GetMapping("/{username}")
        public ResponseEntity<ApiResponse<ProfileDto>> getUserProfileById(@PathVariable String username) {
            try{
                ProfileDto result= userService.getUserProfileById(username);
                return ResponseEntity.ok(ApiResponse.success("取得成功", result));
            } catch (RuntimeException ex) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
            }
        }

}

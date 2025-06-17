package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.*;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.ArticleService;
import com.example.quizz_b.service.QuizService;
import com.example.quizz_b.service.UserService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuizService quizService;

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

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileDto>> updateUserProfile(@RequestBody ProfileRequestDto dto, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        Long userId = jwtUtil.extractUserId(token);
        User user = userService.getById(userId);

        ProfileDto profileDto= userService.updateUserProfile(dto, user);
        return ResponseEntity.ok(ApiResponse.success("更新成功", profileDto));
    }

    @GetMapping("/{username}/articles")
    public ResponseEntity<ApiResponse<List<ArticleListDto>>> getUserArticles(@PathVariable String username) {
        try{
            List<ArticleListDto> articles = articleService.getArticlesByUsername(username);
            return ResponseEntity.ok(ApiResponse.success("取得成功", articles));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @GetMapping("/{username}/quizzes")
    public ResponseEntity<ApiResponse<List<QuizListDto>>> getUserQuizzes(@PathVariable String username) {
        try{
            List<QuizListDto> quizzes = quizService.getQuizzesByUsername(username);
            return ResponseEntity.ok(ApiResponse.success("取得成功", quizzes));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleFollow(@PathVariable Long targetUserId, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        Long userId = jwtUtil.extractUserId(token);
        System.out.println(userId+" "+targetUserId);

        Boolean isFollowing = userService.toggleFollow(userId, targetUserId);
        int followerCount = userService.getFollowerCountById(targetUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("isFollowing", isFollowing);
        result.put("followerCount", followerCount);

        return ResponseEntity.ok(ApiResponse.success("切換成功", result ));
    }


}

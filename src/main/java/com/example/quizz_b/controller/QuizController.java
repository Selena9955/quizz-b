package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.QuizDto;
import com.example.quizz_b.model.dto.QuizListDto;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.QuizService;
import com.example.quizz_b.service.UserService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {
    @Autowired
    private QuizService quizService;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody QuizSubmitRequestDto dto, HttpServletRequest request) {
        try{
            String token = jwtUtil.extractTokenFromRequest(request);
            Long userId = jwtUtil.extractUserId(token);
            User user = userService.getById(userId);

            quizService.create(dto,user);
            return ResponseEntity.ok(ApiResponse.success("新增成功", null));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizListDto>>> getAllQuizzes(){
        try{
            List<QuizListDto> listDtos =  quizService.getAllQuizzes();
            return ResponseEntity.ok(ApiResponse.success("取得成功", listDtos));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizDto>> getQuiz(@PathVariable Long id){
        try{
            QuizDto quizDto =  quizService.getQuizById(id);
            return ResponseEntity.ok(ApiResponse.success("取得成功", quizDto));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }
}

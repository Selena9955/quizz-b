package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.QuizDto;
import com.example.quizz_b.model.dto.QuizListDto;
import com.example.quizz_b.model.dto.QuizRecordRequestDto;
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

import java.util.Map;

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
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllQuizzes( @RequestParam(defaultValue = "ALL") String type,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size){
        try{
            Map<String, Object> listDtos =  quizService.getAllQuizzes(type, page, size);
            return ResponseEntity.ok(ApiResponse.success("取得成功", listDtos));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizDto>> getQuiz(@PathVariable Long id){
        QuizDto quizDto = quizService.getQuizById(id);
        return ResponseEntity.ok(ApiResponse.success("取得成功", quizDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id,HttpServletRequest request){
        try{
            String token = jwtUtil.extractTokenFromRequest(request);
            Long userId = jwtUtil.extractUserId(token);

            quizService.softDeleteQuizById(id, userId);
            return ResponseEntity.ok(ApiResponse.success("刪除成功", null));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(@PathVariable Long id,
                                                    @RequestBody QuizSubmitRequestDto dto,
                                                    HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);
            Long userId = jwtUtil.extractUserId(token);
            User user = userService.getById(userId);

            quizService.update(id, dto, user);
            return ResponseEntity.ok(ApiResponse.success("更新成功", null));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
        }
    }

    @PostMapping("/records")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recordAnswer(@RequestBody QuizRecordRequestDto request,
                                                          HttpServletRequest httpRequest) {
        String token = jwtUtil.extractTokenFromRequest(httpRequest);
        Long userId = jwtUtil.extractUserId(token);

        Map<String, Object> data = quizService.recordAnswer(request,userId);
        return ResponseEntity.ok(ApiResponse.success("答題記錄完成", data));
    }
}

package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.ArticleCreateRequestDto;
import com.example.quizz_b.model.dto.ArticleDetailDto;
import com.example.quizz_b.model.dto.ArticleListDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.ArticleService;
import com.example.quizz_b.service.UserService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createArticle(@Valid @RequestBody ArticleCreateRequestDto body, HttpServletRequest request) {
            try{
                String token = jwtUtil.extractTokenFromRequest(request);
                Long userId = jwtUtil.extractUserId(token);
                User user = userService.getById(userId);

                articleService.create(body,user);
                return ResponseEntity.ok(ApiResponse.success("新增成功", null));
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
            }
    }

    @Transactional
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<ArticleListDto>>> getAllArticle() {
        try{
            List<ArticleListDto> articles =  articleService.getAllArticles();
            return ResponseEntity.ok(ApiResponse.success("取得成功", articles));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> getArticle(@PathVariable Long id) {
        try{
            ArticleDetailDto articles =  articleService.getById(id);
            return ResponseEntity.ok(ApiResponse.success("取得成功", articles));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable Long id, HttpServletRequest request){
        try{
            String token = jwtUtil.extractTokenFromRequest(request);
            Long userId = jwtUtil.extractUserId(token);

            articleService.delete(id,userId);
            return ResponseEntity.ok(ApiResponse.success("刪除成功", null));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<ApiResponse<Void>> updateArticle(@Valid @RequestBody ArticleCreateRequestDto body, @PathVariable Long id, HttpServletRequest request) {
        try{
            String token = jwtUtil.extractTokenFromRequest(request);
            Long userId = jwtUtil.extractUserId(token);
            User user = userService.getById(userId);

            articleService.update(id,body,user);
            return ResponseEntity.ok(ApiResponse.success("新增成功", null));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
        }
    }
}

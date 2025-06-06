package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.ArticleCreateRequestDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createArticle(@Valid @RequestBody ArticleCreateRequestDto body) {
            try{
                articleService.create(body);
                return ResponseEntity.ok(ApiResponse.success("新增成功", null));
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
            }
    }
}

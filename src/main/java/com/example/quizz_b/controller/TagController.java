package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.TagDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<TagDto>>> getTags() {
            try{
                List<TagDto> result= tagService.getAllTags();
                return ResponseEntity.ok(ApiResponse.success("取得成功", result));
            } catch (RuntimeException ex) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
            }
    }
}

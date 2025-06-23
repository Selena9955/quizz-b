package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.TagDetailDto;
import com.example.quizz_b.model.dto.TagDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<TagDetailDto>>> getTags() {
            try{
                List<TagDetailDto> result= tagService.getAllTags();
                return ResponseEntity.ok(ApiResponse.success("取得成功", result));
            } catch (RuntimeException ex) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400,ex.getMessage()));
            }
    }

    @PostMapping("/home-hot")
    public ResponseEntity<ApiResponse<Void>> updateHomeHotTags(@RequestBody List<String> tagNames) {
        tagService.updateHomeHotTags(tagNames);
        return ResponseEntity.ok(ApiResponse.success("更新成功", null));
    }

    @GetMapping("/home-hot")
    public ResponseEntity<ApiResponse<List<TagDto>>> getHomeHotTags() {
        List<TagDto> dtos =  tagService.getHomeHotTags();
        System.out.println(dtos);
        return ResponseEntity.ok(ApiResponse.success("取得成功", dtos));
    }

    @PostMapping("/record-search")
    public ResponseEntity<ApiResponse<Void>> recordSearchTag(@RequestBody List<String> tags) {
        tagService.recordSearchTags(tags);
        return ResponseEntity.ok(ApiResponse.success("取得成功", null));
    }
}

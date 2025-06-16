package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.ProfileDto;
import com.example.quizz_b.model.dto.SearchResultDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResultDto>> search(@RequestParam("q") String keyword) {
        // 這裡的 keyword 是已解碼的，例如 "java+ 測試"
        SearchResultDto dto = searchService.search(keyword);
        System.out.println(dto);
        return ResponseEntity.ok(ApiResponse.success("搜尋成功", dto));
    }

}

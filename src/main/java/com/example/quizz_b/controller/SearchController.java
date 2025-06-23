package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.SearchResultDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.SearchService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResultDto>> search(@RequestParam("q") String keyword,
                                                               @RequestParam(value = "type", required = false) Integer type,
                                                               HttpServletRequest request) {
        String token = jwtUtil.extractTokenIfPresent(request);

        Long userId = null;
        if (token != null && !token.isBlank()) {
            userId = jwtUtil.extractUserId(token);
        }
        SearchResultDto dto = searchService.search(keyword,type,userId);
        System.out.println(dto);
        return ResponseEntity.ok(ApiResponse.success("搜尋成功", dto));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<String>>> getSearchHistory(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        Long userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success("未登入使用者，無搜尋紀錄", List.of()));
        }

        List<String> history = searchService.getRecentKeywords(userId);
        return ResponseEntity.ok(ApiResponse.success("取得成功", history));
    }

}

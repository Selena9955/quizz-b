package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "search:history:";

    public void saveSearchKeyword(Long userId, String keyword) {
        String key = KEY_PREFIX + userId;

        List<String> existingKeywords = redisTemplate.opsForList().range(key, 0, 19);
        if (existingKeywords != null && existingKeywords.contains(keyword)) {
            return; // 已存在，不紀錄
        }

        redisTemplate.opsForList().leftPush(key, keyword);
        redisTemplate.opsForList().trim(key, 0, 19); // 只保留最近 20 筆
    }

    public List<String> getRecentKeywords(Long userId) {
        String key = KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, 19);
    }

    public SearchResultDto search(SearchQueryDto query) {
        String keyword = query.getQ();
        Integer type = query.getType() ;
        Long userId = query.getUserId();

        if (type == null) {
            type = 0; // 預設為查 quiz
        }

        if (userId != null && keyword != null && !keyword.isBlank()) {
            saveSearchKeyword(userId, keyword);
        }

        // 先預設成空陣列
        List<QuizListDto> quizMatches = Collections.emptyList();
        List<ArticleListDto> articleMatches = Collections.emptyList();
        List<UserCardDto> userMatches = Collections.emptyList();


        if (type == 0) {
            quizMatches = quizService.searchByTitle(keyword);
        }
        if (type == 1) {
            articleMatches = articleService.searchByTitle(keyword);
        }
        if (type == 2) {
            userMatches = userService.searchByUsername(keyword);
        }

        return new SearchResultDto(articleMatches, quizMatches, userMatches);
    }
}

package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TagService tagService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "search:history:";

    public void saveSearchKeyword(Long userId, String keyword) {
        String key = KEY_PREFIX + userId;
        redisTemplate.opsForList().leftPush(key, keyword);
        redisTemplate.opsForList().trim(key, 0, 19); // 只保留最近 20 筆
    }

    public List<String> getRecentKeywords(Long userId) {
        String key = KEY_PREFIX + userId;
        return redisTemplate.opsForList().range(key, 0, 19);
    }

    public SearchResultDto search(String keyword , Long userId) {
        if (userId != null) {
            saveSearchKeyword(userId, keyword);
        }

        List<QuizListDto> quizMatches = quizService.searchByTitle(keyword);
        List<ArticleListDto> articleMatches = articleService.searchByTitle(keyword);
        List<TagDetailDto> tagMatches = tagService.searchByName(keyword);

        return new SearchResultDto(articleMatches, quizMatches, tagMatches);
    }
}

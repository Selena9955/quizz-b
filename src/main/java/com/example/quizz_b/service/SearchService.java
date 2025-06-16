package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    public SearchResultDto search(String keyword) {
        List<QuizListDto> quizMatches = quizService.searchByTitle(keyword);
        List<ArticleListDto> articleMatches = articleService.searchByTitle(keyword);
        List<TagDetailDto> tagMatches = tagService.searchByName(keyword);

        return new SearchResultDto(articleMatches, quizMatches, tagMatches);
    }
}

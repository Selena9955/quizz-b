package com.example.quizz_b.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizListDto {
    private Long id;
    private String authorName;
    private String avatarUrl;
    private Integer quizType;
    private String title;
    private List<String> tags;
    private QuizStatsDto quizStats;
}

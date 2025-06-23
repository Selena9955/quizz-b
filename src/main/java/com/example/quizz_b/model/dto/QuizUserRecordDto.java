package com.example.quizz_b.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizUserRecordDto {
    private int totalCount;
    private int correctCount;
    private double correctRate;
    private List<RecentQuizDto> recentQuizzes;


    @Data
    public static class RecentQuizDto {
        private Long id;
        private String title;
    }
}

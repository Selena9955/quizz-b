package com.example.quizz_b.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizRecommendRequest {
    private List<TagDto> tags;
    private Long excludeId;
}

package com.example.quizz_b.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizStatsDto {
    private Long totalCount;
    private Long correctCount;
    private double correctRate;
}

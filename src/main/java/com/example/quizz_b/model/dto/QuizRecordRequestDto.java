package com.example.quizz_b.model.dto;

import lombok.Data;

@Data
public class QuizRecordRequestDto {
    private Long quizId;
    private Boolean isCorrect;
}

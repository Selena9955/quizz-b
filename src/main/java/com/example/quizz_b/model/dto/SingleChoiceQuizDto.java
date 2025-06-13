package com.example.quizz_b.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SingleChoiceQuizDto extends QuizDto {
    private List<QuizOptionDto> options;
    private String singleAnswerId;
}

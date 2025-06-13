package com.example.quizz_b.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MultipleChoiceQuizDto extends QuizDto {
    private List<QuizOptionDto> options;
    private List<String> multipleAnswerId;
}
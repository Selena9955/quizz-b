package com.example.quizz_b.factory.quiz;

import com.example.quizz_b.model.dto.QuizSubmitRequestDto;

public interface QuizHandler {
    void validate(QuizSubmitRequestDto dto);
}

package com.example.quizz_b.factory.quiz.handler;

import com.example.quizz_b.factory.quiz.QuizHandler;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import org.springframework.stereotype.Component;

@Component
public class FlashQuizHandler implements QuizHandler {
    @Override
    public void validate(QuizSubmitRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new IllegalArgumentException("需填寫標題");
        if (dto.getFlashAnswer() == null || dto.getFlashAnswer().isBlank())
            throw new IllegalArgumentException("需填寫解答");
    }
}

package com.example.quizz_b.factory.quiz.handler;

import com.example.quizz_b.factory.quiz.QuizHandler;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import org.springframework.stereotype.Component;

@Component
public class MultipleQuizHandler implements QuizHandler {
    @Override
    public void validate(QuizSubmitRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new IllegalArgumentException("多選題需填寫標題");
        if (dto.getOptions() == null || dto.getOptions().isEmpty())
            throw new IllegalArgumentException("多選題需填寫選項");
        if (dto.getMultipleAnswerId() == null || dto.getMultipleAnswerId().isEmpty())
            throw new IllegalArgumentException("多選題需至少一個正確答案");
    }
}

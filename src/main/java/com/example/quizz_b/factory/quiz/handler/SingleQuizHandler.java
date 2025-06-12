package com.example.quizz_b.factory.quiz.handler;

import com.example.quizz_b.factory.quiz.QuizHandler;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import org.springframework.stereotype.Component;

@Component
public class SingleQuizHandler implements QuizHandler {

    @Override
    public void validate(QuizSubmitRequestDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new IllegalArgumentException("單選題需填寫標題");
        if (dto.getOptions() == null || dto.getOptions().isEmpty())
            throw new IllegalArgumentException("單選題需填寫選項");
        if (dto.getSingleAnswerId() == null || dto.getSingleAnswerId().isBlank())
            throw new IllegalArgumentException("單選題需指定正確答案");
    }
}

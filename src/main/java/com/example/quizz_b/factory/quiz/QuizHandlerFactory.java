package com.example.quizz_b.factory.quiz;

import com.example.quizz_b.constant.enums.QuizType;
import com.example.quizz_b.factory.quiz.handler.FlashQuizHandler;
import com.example.quizz_b.factory.quiz.handler.MultipleQuizHandler;
import com.example.quizz_b.factory.quiz.handler.SingleQuizHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QuizHandlerFactory {
    private final Map<QuizType, QuizHandler> handlers;

    public QuizHandlerFactory(
            SingleQuizHandler single,
            MultipleQuizHandler multiple,
            FlashQuizHandler flash
    ) {
        this.handlers = Map.of(
                QuizType.SINGLE, single,
                QuizType.MULTIPLE, multiple,
                QuizType.FLASH, flash
        );
    }

    public QuizHandler getHandler(QuizType type) {
        QuizHandler handler = handlers.get(type);
        if (handler == null)
            throw new IllegalArgumentException("無效的題型處理器");
        return handler;
    }
}

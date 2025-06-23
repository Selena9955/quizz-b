package com.example.quizz_b.repository.custom;

import com.example.quizz_b.model.entity.Quiz;

import java.util.List;

public interface QuizRepositoryCustom {
    List<Quiz> searchByMultipleKeywords(String[] keywords);
}

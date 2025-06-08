package com.example.quizz_b.service;

import com.example.quizz_b.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;


}

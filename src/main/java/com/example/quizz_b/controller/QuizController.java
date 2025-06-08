package com.example.quizz_b.controller;

import com.example.quizz_b.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuizController {
    @Autowired
    private QuizService quizService;

}

package com.example.quizz_b.model.dto;

import com.example.quizz_b.model.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class QuizListDto {
    private Long id;
    private String authorName;
    private Integer quizType;
    private String title;
    private List<String> tags;
}

package com.example.quizz_b.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
    private Long id;
    private String name;
    private long countArticles = 0;
    private long countQuizzes = 0;
}

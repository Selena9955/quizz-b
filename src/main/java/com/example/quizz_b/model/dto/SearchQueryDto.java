package com.example.quizz_b.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SearchQueryDto {
    private Long userId;
    private String q;
    private Integer type; // 0: quiz, 1: article, 2: tag
}

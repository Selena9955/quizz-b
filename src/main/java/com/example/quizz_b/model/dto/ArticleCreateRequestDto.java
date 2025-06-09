package com.example.quizz_b.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateRequestDto {
    private String title;
    private String content;
    private String previewContent;
    private List<String> tags;
}

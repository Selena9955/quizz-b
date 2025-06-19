package com.example.quizz_b.model.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDto {
    private List<ArticleListDto> articles; // "article", "quiz", "tag"
    private List<QuizListDto> quizzes;  // tag、文章或題目的 id
    private List<UserCardDto> users;
}
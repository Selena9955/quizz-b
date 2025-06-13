package com.example.quizz_b.model.dto;

import com.example.quizz_b.constant.enums.QuizType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public abstract class QuizDto {
    private Long id;
    private int quizType;
    private String title;
    private String titleDetail;
    private String answerDetail;
    private List<TagDto> tags;
    private Long authorId;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

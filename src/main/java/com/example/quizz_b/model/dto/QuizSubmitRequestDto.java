package com.example.quizz_b.model.dto;

import com.example.quizz_b.model.entity.QuizOption;
import lombok.Data;

import java.util.List;

@Data
public class QuizSubmitRequestDto {
    private Integer quizType;
    private String title;
    private String titleDetail;
    private List<QuizOption> options;
    private String singleAnswerId;
    private List<String> multipleAnswerId;
    private String flashAnswer;
    private String answerDetail;
    private List<String> tags;
}

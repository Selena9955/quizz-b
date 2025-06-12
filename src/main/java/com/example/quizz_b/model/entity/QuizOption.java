package com.example.quizz_b.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class QuizOption {
    // UUID 由前端產生
    private String id;

    private String text;

}

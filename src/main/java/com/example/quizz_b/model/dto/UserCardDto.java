package com.example.quizz_b.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCardDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String bio;
}

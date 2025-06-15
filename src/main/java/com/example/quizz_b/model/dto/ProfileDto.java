package com.example.quizz_b.model.dto;

import lombok.Data;

@Data
public class ProfileDto {
    private Long id;
    private String username;
    private String bio;
    private String avatarUrl;
    private String profileBgUrl;
    private int quizCount;
    private int articleCount;
    private int followers;
}

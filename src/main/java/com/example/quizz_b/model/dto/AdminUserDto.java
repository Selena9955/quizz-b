package com.example.quizz_b.model.dto;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

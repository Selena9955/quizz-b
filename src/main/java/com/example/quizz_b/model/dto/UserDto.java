package com.example.quizz_b.model.dto;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UserStatus status;
    private UserRole role;

}

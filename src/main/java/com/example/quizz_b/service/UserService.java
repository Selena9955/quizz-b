package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.UserDto;

public interface UserService {
    void createUser(String username, String password, String email);
    boolean isEmailRegistered(String email);
    boolean isUsernameTaken(String username);
}

package com.example.quizz_b.service.impl;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import com.example.quizz_b.model.dto.UserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import com.example.quizz_b.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    // 密碼加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @Override
        public void createUser(String username, String password, String email) {
            // 檢查 email 是否重複
            Optional<User> optEmail=userRepository.findByEmail(email);
            if(optEmail.isPresent()){
                throw new RuntimeException("Email already exists");
            }

            // 檢查 username 是否重複
            Optional<User> optUsername = userRepository.findByUsername(username);
            if (optUsername.isPresent()) {
                throw new RuntimeException("Username already exists");
            }

            // 密碼加密
            String passwordHash = passwordEncoder.encode(password);

            // 建立新使用者
            User user = new User();
            user.setPassword(passwordHash);
            user.setEmail(email);
            user.setUsername(username);
            user.setStatus(UserStatus.UNVERIFIED);
            user.setRole(UserRole.USER);

            userRepository.save(user);
        }
}

package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import com.example.quizz_b.model.dto.UserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import com.example.quizz_b.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 密碼加密器
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        UserDto dto = convertToDto(user);
        String token = jwtUtil.generateToken(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", dto);
        return result;
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        return dto;
    }

    private User convertToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        user.setRole(dto.getRole());
        return user;
    }
}

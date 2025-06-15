package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import com.example.quizz_b.model.dto.ProfileDto;
import com.example.quizz_b.model.dto.UserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import com.example.quizz_b.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

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

    public UserDto getUserDtoByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
        return  convertToDto(user);
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
    }

    public UserDto getUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
        return  convertToDto(user);
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        String code = String.valueOf((int)(Math.random() * 900000) + 100000); // 六位數
        user.setVerificationCode(code);
        user.setCodeGeneratedAt(LocalDateTime.now());
        userRepository.save(user);
        String body = String.format("""
        歡迎註冊 Quizz 會員

        您的驗證碼為：%s

        """, code);

        emailService.send(email, "歡迎註冊 Quizz 會員", body);
    }
    public void checkVerificationCode(String email,String code){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
        String verificationCode = user.getVerificationCode();
        if (verificationCode == null || !verificationCode.equals(code)) {
            throw new RuntimeException("驗證碼錯誤");
        }
        if (user.getCodeGeneratedAt() == null ||
                Duration.between(user.getCodeGeneratedAt(), LocalDateTime.now()).toMinutes() > 10) {
            throw new RuntimeException("驗證碼已過期");
        }
        user.setStatus(UserStatus.VERIFIED);
        userRepository.save(user);
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

    public ProfileDto getUserProfileById(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("使用者不存在"));

        ProfileDto dto = new ProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setProfileBgUrl(user.getProfileBgUrl());
        dto.setFollowers(0);
        dto.setArticleCount(1905);
        dto.setQuizCount(0);

        return dto;
    }

    public void updateUserProfile(User user) {
        System.out.println(user);
    }
}

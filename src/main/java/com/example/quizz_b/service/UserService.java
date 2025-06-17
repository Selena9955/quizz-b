package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import com.example.quizz_b.model.dto.ProfileDto;
import com.example.quizz_b.model.dto.ProfileRequestDto;
import com.example.quizz_b.model.dto.UserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import com.example.quizz_b.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private QuizService quizService;

    @Autowired
    private ArticleService articleService;

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
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setProfileBgUrl(user.getProfileBgUrl());

        return dto;
    }


    private ProfileDto convertToProfileDto(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setProfileBgUrl(user.getProfileBgUrl());
        dto.setFollowers(0);       // TODO: 實作 followers 數量邏輯
        dto.setArticleCount(articleService.getArticleCountByUserId(user.getId()));
        dto.setQuizCount(quizService.getQuizCountByUserId(user.getId()));

        return dto;
    }

    public ProfileDto getUserProfileById(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("使用者不存在"));
        return convertToProfileDto(user);
    }

    public ProfileDto updateUserProfile(ProfileRequestDto formData, User user) {
        //用戶名有更改
        if (!formData.getUsername().equals(user.getUsername())) {
            String newUsername = formData.getUsername();

            // 長度限制
            if (newUsername.length() < 2 || newUsername.length() > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用戶名長度需為 2～50 字之間");
            }

            // 重複性檢查
            if (isUsernameTaken(newUsername)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "用戶名已被使用");
            }

            user.setUsername(newUsername);
        }

        String base64Avatar = formData.getAvatarUrl();
        String base64ProfileBg = formData.getProfileBgUrl();

        if(base64Avatar!= null && base64Avatar.startsWith("data:image/")){
            user.setAvatarUrl(base64Avatar);
        }
        if(base64ProfileBg!=null && base64ProfileBg.startsWith("data:image/")){
            user.setProfileBgUrl(base64ProfileBg);
        }

        user.setBio(formData.getBio());
        userRepository.save(user);
        return convertToProfileDto(user);
    }
}

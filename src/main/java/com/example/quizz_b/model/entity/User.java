package com.example.quizz_b.model.entity;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(length = 50,  nullable = false,unique = true)
    @Size(min = 2, message = "用戶名長度至少 2 個字")
    private String username;

    @Column(nullable = false)
    @Size(min = 6, message = "密碼長度至少 6 個字")
    private String password;

    @Column(nullable = false,unique = true)
    private String email;

    @Enumerated(EnumType.ORDINAL) //儲存數字
    private UserStatus status = UserStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "code_generated_at")
    private LocalDateTime codeGeneratedAt;
}

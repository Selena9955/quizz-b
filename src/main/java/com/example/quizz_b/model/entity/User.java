package com.example.quizz_b.model.entity;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @ToString.Include
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

    @ToString.Exclude
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Article> articles = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Quiz> quizzes = new ArrayList<>();

    @Lob
    @Column(name = "avatar_url", columnDefinition = "LONGTEXT")
    private String avatarUrl;

    @Lob
    @Column(name = "profile_bg_url", columnDefinition = "LONGTEXT")
    private String profileBgUrl;

    @Size(max = 200)
    private String bio;
}

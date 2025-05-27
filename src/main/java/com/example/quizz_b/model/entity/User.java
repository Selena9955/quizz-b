package com.example.quizz_b.model.entity;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.constant.enums.UserStatus;
import jakarta.persistence.*;
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
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(length = 50,  nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    private String email;

    @Enumerated(EnumType.ORDINAL) //儲存數字
    private UserStatus status = UserStatus.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;


}

package com.example.quizz_b.model.entity;

import com.example.quizz_b.constant.enums.QuizType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Enumerated(EnumType.ORDINAL)
    private QuizType quizType;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String titleDetail;

    @ManyToMany
    @JoinTable(
            name = "quiz_tags",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // 選項-單選題、多選題
    @ElementCollection
    @CollectionTable(name = "quiz_options", joinColumns = @JoinColumn(name = "quiz_id"))
    private List<QuizOption> options;

    // 解答-單選題
    private String singleAnswerId;

    // 解答-多選題
    @ElementCollection
    @CollectionTable(name = "quiz_multiple_answer", joinColumns = @JoinColumn(name = "quiz_id"))
    private List<String> multipleAnswerId;

    // 解答-記憶題
    private String flashAnswer;

    private String answerDetail;

}

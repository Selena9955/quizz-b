package com.example.quizz_b.model.entity;

import com.example.quizz_b.constant.enums.QuizType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="quiz")
public class Quiz {
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

    @Enumerated(EnumType.ORDINAL)
    private QuizType quizType;

    @ToString.Include
    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String titleDetail;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "quiz_tags",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // 選項-單選題、多選題
    @ToString.Exclude
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "position")
    private List<QuizOption> options = new ArrayList<>();

    // 解答-單選題
    private String singleAnswerId;

    // 解答-多選題
    @ElementCollection
    @CollectionTable(name = "quiz_multiple_answer", joinColumns = @JoinColumn(name = "quiz_id"))
    private Set<String> multipleAnswerId;

    // 解答-記憶題
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String flashAnswer;

    private String answerDetail;

    @Column(name = "is_delete")
    private boolean isDelete  = false;

    // 所有答題記錄
    @ToString.Exclude
    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY)
    private List<QuizRecord> records = new ArrayList<>();
}

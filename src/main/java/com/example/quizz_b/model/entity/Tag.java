package com.example.quizz_b.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();

    @ManyToMany(mappedBy = "tags")
    private Set<Quiz> quizzes = new HashSet<>();

    @Column(nullable = false)
    private long countArticle = 0;

    @Column(nullable = false)
    private long countQuizzes= 0;

    // 處理 Set 內的資料 在 stream 中修改時造成的 ConcurrentModificationException
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return name != null && name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

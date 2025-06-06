package com.example.quizz_b.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String title;
    private String content;
    private Set<TagDto> tags = new HashSet<>();
}

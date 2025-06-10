package com.example.quizz_b.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDetailDto {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String title;
    private String content;
    private List<String> tags = new ArrayList<>();
    private AuthorDto author;
}

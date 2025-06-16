package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    List<Article> findByAuthorId(Long userId);

    int countByAuthorId(Long authorId);

    List<Article> findByTitleContainingIgnoreCase(String keyword);
}

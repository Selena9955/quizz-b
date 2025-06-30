package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.Article;
import com.example.quizz_b.model.entity.Quiz;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    List<Article> findByAuthorId(Long userId);

    List<Article> findByAuthorIdAndIsDeleteFalse(Long userId);

    int countByAuthorId(Long authorId);

    List<Article> findByTitleContainingIgnoreCase(String keyword);

    List<Article> findByIsDeleteFalse(Sort sort);

}

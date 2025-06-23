package com.example.quizz_b.repository;

import com.example.quizz_b.constant.enums.QuizType;
import com.example.quizz_b.model.entity.Quiz;
import com.example.quizz_b.repository.custom.QuizRepositoryCustom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> , QuizRepositoryCustom {
    @EntityGraph(attributePaths = {"tags", "author", "options"})
    List<Quiz> findAllByOrderByCreateTimeDesc();

    @EntityGraph(attributePaths = {"tags", "author", "options", "multipleAnswerId"})
    Optional<Quiz> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"tags", "author"})
    List<Quiz> findAllByIsDeleteFalseOrderByCreateTimeDesc();

    List<Quiz> findByAuthorId(Long userId);

    int countByAuthorId(Long userId);

    List<Quiz> findByTitleContainingIgnoreCaseAndIsDeleteFalse(String keyword);

    @Query("SELECT q FROM Quiz q WHERE q.isDelete = false ORDER BY q.createTime DESC")
    Page<Quiz> findAllVisible(Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.isDelete = false AND q.quizType = :quizType ORDER BY q.createTime DESC")
    Page<Quiz> findAllVisibleByType(@Param("quizType") QuizType quizType, Pageable pageable);

    @Query(value = "SELECT * FROM quiz ORDER BY create_time DESC LIMIT :limit", nativeQuery = true)
    List<Quiz> findLatest(@Param("limit") int limit);
}

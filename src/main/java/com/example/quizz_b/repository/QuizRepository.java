package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> {
    @EntityGraph(attributePaths = {"tags", "author", "options"})
    List<Quiz> findAllByOrderByCreateTimeDesc();

    @EntityGraph(attributePaths = {"tags", "author", "options", "multipleAnswerId"})
    Optional<Quiz> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"tags", "author"})
    List<Quiz> findAllByIsDeleteFalseOrderByCreateTimeDesc();
}

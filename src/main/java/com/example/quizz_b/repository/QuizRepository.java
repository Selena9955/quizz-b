package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> {
    List<Quiz> findAllByOrderByCreateTimeDesc();
}

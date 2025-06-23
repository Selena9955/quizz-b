package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.QuizRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRecordRepository extends JpaRepository<QuizRecord,Long> {

    Long countByQuizId(Long id);

    Long countByQuizIdAndIsCorrectTrue(Long id);

    Long countByUserId(Long userId);

    Long countByUserIdAndIsCorrectTrue(Long userId);

    List<QuizRecord> findTop3ByUserIdOrderByCreateTimeDesc(Long userId);
}

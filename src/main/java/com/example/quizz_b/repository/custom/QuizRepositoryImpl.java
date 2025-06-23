package com.example.quizz_b.repository.custom;

import com.example.quizz_b.model.entity.Quiz;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class QuizRepositoryImpl implements QuizRepositoryCustom{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Quiz> searchByMultipleKeywords(String[] keywords) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Quiz> cq = cb.createQuery(Quiz.class);
        Root<Quiz> root = cq.from(Quiz.class);

        List<Predicate> predicates = new ArrayList<>();

        // 篩掉已刪除的
        predicates.add(cb.isFalse(root.get("isDelete")));

        // 為每個 keyword 加入模糊比對
        for (String keyword : keywords) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Quiz> query = em.createQuery(cq);

        return query.getResultList();
    }
}

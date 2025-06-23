package com.example.quizz_b.repository.custom;

import com.example.quizz_b.model.entity.Quiz;
import com.example.quizz_b.model.entity.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
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

        // 避免重複結果
        cq.distinct(true);

        // 篩掉已刪除的 is_delete = false
        Predicate isNotDeleted = cb.isFalse(root.get("isDelete"));

        // 條件一：所有 keyword 都出現在 title 中（AND 條件）
        List<Predicate> titleConditions = Arrays.stream(keywords)
                .map(kw -> cb.like(cb.lower(root.get("title")), "%" + kw.toLowerCase() + "%"))
                .toList();
        Predicate allInTitle = cb.and(titleConditions.toArray(new Predicate[0]));

        // 條件二：所有 keyword 都出現在 tags.name 中（每個關鍵字用一個 exists 子查詢）
        List<Predicate> allInTags = new ArrayList<>();
        for (String kw : keywords) {
            Subquery<Long> sq = cq.subquery(Long.class);
            Root<Quiz> sqRoot = sq.from(Quiz.class);
            Join<Quiz, Tag> sqTags = sqRoot.join("tags");

            Predicate matchId = cb.equal(sqRoot.get("id"), root.get("id")); // 關聯相同 quiz
            Predicate likeTag = cb.like(cb.lower(sqTags.get("name")), "%" + kw.toLowerCase() + "%");

            // 子查詢：是否存在某個 tag.name 含有該關鍵字
            sq.select(cb.literal(1L)).where(cb.and(matchId, likeTag));
            allInTags.add(cb.exists(sq));
        }

        Predicate allKeywordsInTags = cb.and(allInTags.toArray(new Predicate[0]));

        // 組合查詢條件：未刪除 AND (全部 keyword 在 title OR 全部 keyword 在 tags)
        cq.where(cb.and(isNotDeleted, cb.or(allInTitle, allKeywordsInTags)));

        // 建立時間降冪排序（最新的 quiz 在前）
        cq.orderBy(cb.desc(root.get("createTime")));

        return em.createQuery(cq).getResultList();
    }

}

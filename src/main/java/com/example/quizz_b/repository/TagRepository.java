package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.Tag;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE Tag t SET t.homeHotOrder = NULL WHERE t.homeHotOrder IS NOT NULL")
    void clearHomeHotOrder();

    @Modifying
    @Transactional
    @Query("UPDATE Tag t SET t.homeHotOrder = :order WHERE t.name = :name")
    void updateHomeHotOrderByName(@Param("name") String name, @Param("order") int order);

    // 取得熱門標籤，照順序排序
    List<Tag> findByHomeHotOrderIsNotNullOrderByHomeHotOrderAsc();
}

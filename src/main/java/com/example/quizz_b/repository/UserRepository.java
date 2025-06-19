package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @Query(value = """
      SELECT * FROM users 
      WHERE LOWER(username) LIKE LOWER(CONCAT('%', :keyword, '%'))
      ORDER BY 
        CASE WHEN LOWER(username) = LOWER(:keyword) THEN 0 ELSE 1 END,
        username
""", nativeQuery = true)
    List<User> searchByUsername(@Param("keyword") String keyword);
}

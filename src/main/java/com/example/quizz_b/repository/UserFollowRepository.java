package com.example.quizz_b.repository;

import com.example.quizz_b.model.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow,Long> {
    Optional<UserFollow> findByUserIdAndFollowingId(Long userId, Long targetUserId);

    int countByFollowingId(Long userId);

}

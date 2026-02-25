package com.example.muse.repository;

import com.example.muse.domain.Review;
import com.example.muse.dto.MyReviewItemResponse;
import com.example.muse.dto.ReviewTodoItemResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByVisitedId(Long visitedId);

    Optional<Review> findByVisitedId(Long visitedId);

    long countByUserId(Long userId);

    // ✅ A) 작성 가능 목록 = visited는 있고 review는 없는 것
    @Query(value = """
        SELECT
            v.id             AS visitedId,
            v.performance_id AS performanceId,
            p.title          AS title,
            p.poster_url     AS posterUrl,
            p.start_date     AS startDate,
            p.end_date       AS endDate,
            v.visited_at     AS visitedAt
        FROM visited v
        JOIN performance p ON p.id = v.performance_id
        LEFT JOIN review r ON r.visited_id = v.id
        WHERE v.user_id = :userId
          AND r.id IS NULL
        ORDER BY v.visited_at DESC, v.id DESC
        """, nativeQuery = true)
    List<ReviewTodoItemResponse> findTodos(@Param("userId") Long userId);

    // ✅ B) 내가 쓴 후기 목록
    @Query(value = """
        SELECT
            r.id             AS reviewId,
            r.visited_id     AS visitedId,
            r.performance_id AS performanceId,
            p.title          AS title,
            p.poster_url     AS posterUrl,
            p.start_date     AS startDate,
            p.end_date       AS endDate,
            r.rating         AS rating,
            r.content        AS content,
            r.created_at     AS createdAt,
            r.updated_at     AS updatedAt,
            v.visited_at     AS visitedAt
        FROM review r
        JOIN performance p ON p.id = r.performance_id
        JOIN visited v     ON v.id = r.visited_id
        WHERE r.user_id = :userId
        ORDER BY r.id DESC
        """, nativeQuery = true)
    List<MyReviewItemResponse> findMine(@Param("userId") Long userId);
}
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
    // visited.event_id  -> event.id JOIN
    @Query(value = """
        SELECT
            v.id           AS visitedId,
            v.event_id     AS eventId,
            e.title        AS title,
            e.image_url    AS imageUrl,
            e.place_name   AS placeName,
            e.start_date   AS startDate,
            e.end_date     AS endDate,
            v.visited_at   AS visitedAt
        FROM visited v
        JOIN event e ON e.id = v.event_id
        LEFT JOIN review r ON r.visited_id = v.id
        WHERE v.user_id = :userId
          AND r.id IS NULL
        ORDER BY v.visited_at DESC, v.id DESC
        """, nativeQuery = true)
    List<ReviewTodoItemResponse> findTodos(@Param("userId") Long userId);

    // ✅ B) 내가 쓴 후기 목록
    // ⚠️ 여기서 review 테이블에 event_id가 "있으면" 아래 쿼리가 바로 동작.
    // 만약 review에 event_id가 없다면, 아래 쿼리에서 r.event_id 부분을 지우고
    // visited v 를 통해 v.event_id로 event를 조인하도록 바꿔야 함(아래에 대안도 같이 적어둠).
    @Query(value = """
        SELECT
            r.id           AS reviewId,
            r.visited_id   AS visitedId,
            r.event_id     AS eventId,
            e.title        AS title,
            e.image_url    AS imageUrl,
            e.place_name   AS placeName,
            e.start_date   AS startDate,
            e.end_date     AS endDate,
            r.rating       AS rating,
            r.content      AS content,
            r.created_at   AS createdAt,
            r.updated_at   AS updatedAt,
            v.visited_at   AS visitedAt
        FROM review r
        JOIN event e   ON e.id = r.event_id
        JOIN visited v ON v.id = r.visited_id
        WHERE r.user_id = :userId
        ORDER BY r.id DESC
        """, nativeQuery = true)
    List<MyReviewItemResponse> findMine(@Param("userId") Long userId);

    /*
    // ✅ B) 대안: review 테이블에 event_id가 "없을 때" 이 쿼리로 바꿔서 사용
    @Query(value = """
        SELECT
            r.id           AS reviewId,
            r.visited_id   AS visitedId,
            v.event_id     AS eventId,
            e.title        AS title,
            e.image_url    AS imageUrl,
            e.place_name   AS placeName,
            e.start_date   AS startDate,
            e.end_date     AS endDate,
            r.rating       AS rating,
            r.content      AS content,
            r.created_at   AS createdAt,
            r.updated_at   AS updatedAt,
            v.visited_at   AS visitedAt
        FROM review r
        JOIN visited v ON v.id = r.visited_id
        JOIN event e   ON e.id = v.event_id
        WHERE r.user_id = :userId
        ORDER BY r.id DESC
        """, nativeQuery = true)
    List<MyReviewItemResponse> findMine(@Param("userId") Long userId);
    */
}
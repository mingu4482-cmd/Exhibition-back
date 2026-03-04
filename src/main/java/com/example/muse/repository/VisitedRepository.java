package com.example.muse.repository;

import com.example.muse.domain.Visited;
import com.example.muse.dto.VisitedItemResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VisitedRepository extends JpaRepository<Visited, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<Visited> findByUserIdAndEventId(Long userId, Long eventId);

    List<Visited> findAllByUserIdOrderByIdDesc(Long userId);

    long countByUserId(Long userId);

    // ✅ 방문 목록(제목/포스터 포함) - visited + event JOIN
    @Query(value = """
        SELECT
            v.event_id     AS eventId,
            e.title        AS title,
            e.image_url    AS imageUrl,
            e.place_name   AS placeName,
            e.start_date   AS startDate,
            e.end_date     AS endDate,
            v.visited_at   AS visitedAt
        FROM visited v
        JOIN event e ON e.id = v.event_id
        WHERE v.user_id = :userId
        ORDER BY v.id DESC
        """, nativeQuery = true)
    List<VisitedItemResponse> findMyVisitedItems(@Param("userId") Long userId);
}
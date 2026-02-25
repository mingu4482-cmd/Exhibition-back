package com.example.muse.repository;

import com.example.muse.domain.Visited;
import com.example.muse.dto.VisitedItemResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VisitedRepository extends JpaRepository<Visited, Long> {

    boolean existsByUserIdAndPerformanceId(Long userId, Long performanceId);

    Optional<Visited> findByUserIdAndPerformanceId(Long userId, Long performanceId);

    List<Visited> findAllByUserIdOrderByIdDesc(Long userId);

    long countByUserId(Long userId);

    // ✅ 방문 목록(제목/포스터 포함)
    @Query(value = """
        SELECT
            v.performance_id AS performanceId,
            p.title          AS title,
            p.poster_url     AS posterUrl,
            p.start_date     AS startDate,
            p.end_date       AS endDate,
            v.visited_at     AS visitedAt
        FROM visited v
        JOIN performance p ON p.id = v.performance_id
        WHERE v.user_id = :userId
        ORDER BY v.id DESC
        """, nativeQuery = true)
    List<VisitedItemResponse> findMyVisitedItems(@Param("userId") Long userId);
}
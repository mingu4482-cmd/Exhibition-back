package com.example.muse.repository;

import com.example.muse.domain.Favorite;
import com.example.muse.dto.FavoriteItemResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndPerformanceId(Long userId, Long performanceId);

    Optional<Favorite> findByUserIdAndPerformanceId(Long userId, Long performanceId);

    List<Favorite> findAllByUserIdOrderByIdDesc(Long userId);

    long countByUserId(Long userId);

    // ✅ 찜 목록(제목/포스터 포함) - favorite + performance JOIN
    @Query(value = """
        SELECT
            f.performance_id AS performanceId,
            p.title          AS title,
            p.poster_url     AS posterUrl,
            p.start_date     AS startDate,
            p.end_date       AS endDate
        FROM favorite f
        JOIN performance p ON p.id = f.performance_id
        WHERE f.user_id = :userId
        ORDER BY f.id DESC
        """, nativeQuery = true)
    List<FavoriteItemResponse> findMyFavoriteItems(@Param("userId") Long userId);
}
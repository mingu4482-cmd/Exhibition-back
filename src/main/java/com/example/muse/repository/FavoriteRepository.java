package com.example.muse.repository;

import com.example.muse.domain.Favorite;
import com.example.muse.dto.FavoriteItemResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    Optional<Favorite> findByUserIdAndEventId(Long userId, Long eventId);

    List<Favorite> findAllByUserIdOrderByIdDesc(Long userId);

    long countByUserId(Long userId);

    // ✅ 찜 목록 (favorite + event JOIN)
    @Query(value = """
        SELECT
            f.event_id      AS eventId,
            e.title         AS title,
            e.image_url     AS imageUrl,
            e.place_name    AS placeName,
            e.start_date    AS startDate,
            e.end_date      AS endDate
        FROM favorite f
        JOIN event e ON e.id = f.event_id
        WHERE f.user_id = :userId
        ORDER BY f.id DESC
        """, nativeQuery = true)
    List<FavoriteItemResponse> findMyFavoriteItems(@Param("userId") Long userId);
}
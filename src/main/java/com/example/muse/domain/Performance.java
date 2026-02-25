package com.example.muse.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "performance",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_source_source_id",
                columnNames = {"source", "source_id"}
        ),
        indexes = {
                @Index(name = "idx_start_date", columnList = "start_date"),
                @Index(name = "idx_area", columnList = "area"),
                @Index(name = "idx_genre", columnList = "genre")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 원천 구분: KOPIS / SEOUL / TEST
    @Column(nullable = false, length = 20)
    private String source;

    // 원천 고유 ID (KOPIS: mt20id, SEOUL: 고유키 없으면 임시로 만들어도 됨)
    @Column(name = "source_id", nullable = false, length = 100)
    private String sourceId;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "venue_name", length = 200)
    private String venueName;

    @Column(length = 50)
    private String area;

    @Column(length = 80)
    private String genre;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    // 지도용 좌표(나중에 지오코딩 붙이면 채움)
    private Double latitude;
    private Double longitude;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

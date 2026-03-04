package com.example.muse.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "review",
        uniqueConstraints = @UniqueConstraint(name = "uk_review_visited", columnNames = {"visited_id"}),
        indexes = {
                @Index(name = "idx_review_user", columnList = "user_id"),
                @Index(name = "idx_review_perf", columnList = "event_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="visited_id", nullable = false)
    private Long visitedId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="event_id", nullable = false)
    private Long eventId;

    private Integer rating; // 1~5

    @Column(length = 2000)
    private String content;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
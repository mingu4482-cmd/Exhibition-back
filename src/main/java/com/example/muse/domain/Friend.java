package com.example.muse.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "friends",
        uniqueConstraints = @UniqueConstraint(name = "uk_friends_pair", columnNames = {"user_id", "friend_user_id"}),
        indexes = {
                @Index(name = "idx_friends_user", columnList = "user_id"),
                @Index(name = "idx_friends_friend", columnList = "friend_user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="friend_user_id", nullable = false)
    private Long friendUserId;

    @Column(name="friend_name", length = 30)
    private String friendName;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
package com.example.muse.repository;

import com.example.muse.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByUserIdAndFriendUserId(Long userId, Long friendUserId);

    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    List<Friend> findAllByUserIdOrderByIdDesc(Long userId);
}
package com.example.muse.service;

import com.example.muse.domain.User;
import com.example.muse.domain.Visited;
import com.example.muse.dto.VisitedItemResponse;
import com.example.muse.repository.UserRepository;
import com.example.muse.repository.VisitedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitedService {

    private final VisitedRepository visitedRepository;
    private final UserRepository userRepository;

    private Long userIdByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return u.getId();
    }

    @Transactional
    public void addVisited(String email, Long EventId) {
        Long userId = userIdByEmail(email);

        if (visitedRepository.existsByUserIdAndEventId(userId, EventId)) {
            return; // 이미 다녀온 기록이면 통과(원하면 에러 처리 가능)
        }

        Visited v = new Visited();
        v.setUserId(userId);
        v.setEventId(EventId);
        v.setVisitedAt(LocalDateTime.now());

        visitedRepository.save(v);
    }

    @Transactional
    public void removeVisited(String email, Long EventId) {
        Long userId = userIdByEmail(email);

        visitedRepository.findByUserIdAndEventId(userId, EventId)
                .ifPresent(visitedRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<VisitedItemResponse> listVisited(String email) {
        Long userId = userIdByEmail(email);
        return visitedRepository.findMyVisitedItems(userId);
    }

    @Transactional(readOnly = true)
    public long countVisited(String email) {
        Long userId = userIdByEmail(email);
        return visitedRepository.countByUserId(userId);
    }
}
package com.example.muse.service;

import com.example.muse.domain.Favorite;
import com.example.muse.domain.User;
import com.example.muse.dto.FavoriteItemResponse;
import com.example.muse.repository.FavoriteRepository;
import com.example.muse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    private Long userIdByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return u.getId();
    }

    @Transactional
    public void addFavorite(String email, Long performanceId) {
        Long userId = userIdByEmail(email);

        if (favoriteRepository.existsByUserIdAndPerformanceId(userId, performanceId)) {
            return;
        }
        Favorite f = new Favorite();
        f.setUserId(userId);
        f.setPerformanceId(performanceId);
        f.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(f);
    }

    @Transactional
    public void removeFavorite(String email, Long performanceId) {
        Long userId = userIdByEmail(email);

        favoriteRepository.findByUserIdAndPerformanceId(userId, performanceId)
                .ifPresent(favoriteRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<FavoriteItemResponse> listFavoriteItems(String email) {
        Long userId = userIdByEmail(email);
        return favoriteRepository.findMyFavoriteItems(userId);
    }

    @Transactional(readOnly = true)
    public long countFavorites(String email) {
        Long userId = userIdByEmail(email);
        return favoriteRepository.countByUserId(userId);
    }
}
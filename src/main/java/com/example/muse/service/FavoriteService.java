package com.example.muse.service;

import com.example.muse.domain.Favorite;
import com.example.muse.dto.FavoriteItemResponse;
import com.example.muse.repository.EventRepository;
import com.example.muse.repository.FavoriteRepository;
import com.example.muse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void addFavorite(String email, Long eventId) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // ✅ eventId가 Spring이 보는 DB에 진짜 존재하는지 먼저 체크 (없으면 404로 끝)
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + eventId);
        }

        // ✅ 이미 찜했으면 조용히 OK (중복 insert로 500 나는 거 방지)
        if (favoriteRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            return;
        }

        Favorite fav = new Favorite();
        fav.setUserId(user.getId());
        fav.setEventId(eventId);
        favoriteRepository.save(fav);
    }

    @Transactional
    public void removeFavorite(String email, Long eventId) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        var fav = favoriteRepository.findByUserIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        favoriteRepository.delete(fav);
    }

    @Transactional(readOnly = true)
    public List<FavoriteItemResponse> listFavoriteItems(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return favoriteRepository.findMyFavoriteItems(user.getId());
    }

    @Transactional(readOnly = true)
    public long countFavorites(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return favoriteRepository.countByUserId(user.getId());
    }
}
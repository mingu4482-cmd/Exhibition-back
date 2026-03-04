package com.example.muse.controller;

import com.example.muse.dto.FavoriteItemResponse;
import com.example.muse.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // ✅ 찜 추가: POST /api/favorites?eventId=388
    @PostMapping
    public Map<String, Object> add(@RequestParam Long eventId, Authentication auth) {
        String email = auth.getName(); // ✅ 캐스팅 금지(500 방지)
        favoriteService.addFavorite(email, eventId);
        return Map.of("ok", true);
    }

    // ✅ 찜 삭제: DELETE /api/favorites?eventId=388
    @DeleteMapping
    public Map<String, Object> remove(@RequestParam Long eventId, Authentication auth) {
        String email = auth.getName(); // ✅ 캐스팅 금지(500 방지)
        favoriteService.removeFavorite(email, eventId);
        return Map.of("ok", true);
    }

    // ✅ 찜 목록: GET /api/favorites
    @GetMapping
    public List<FavoriteItemResponse> list(Authentication auth) {
        String email = auth.getName(); // ✅ 캐스팅 금지(500 방지)
        return favoriteService.listFavoriteItems(email);
    }

    // ✅ 찜 개수: GET /api/favorites/count
    @GetMapping("/count")
    public Map<String, Object> count(Authentication auth) {
        String email = auth.getName(); // ✅ 캐스팅 금지(500 방지)
        return Map.of("count", favoriteService.countFavorites(email));
    }
}
package com.example.muse.controller;

import com.example.muse.dto.FavoriteItemResponse;
import com.example.muse.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public Map<String, Object> add(@RequestParam Long eventId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        favoriteService.addFavorite(email, eventId);
        return Map.of("ok", true);
    }

    @DeleteMapping
    public Map<String, Object> remove(@RequestParam Long eventId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        favoriteService.removeFavorite(email, eventId);
        return Map.of("ok", true);
    }

    @GetMapping
    public List<FavoriteItemResponse> list(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return favoriteService.listFavoriteItems(email);
    }

    @GetMapping("/count")
    public Map<String, Object> count(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return Map.of("count", favoriteService.countFavorites(email));
    }
}
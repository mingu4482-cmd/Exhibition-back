package com.example.muse.controller;

import com.example.muse.dto.VisitedItemResponse;
import com.example.muse.service.VisitedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visited")
public class VisitedController {

    private final VisitedService visitedService;

    // ✅ 다녀온 전시 추가: POST /api/visited?EventId=388
    @PostMapping
    public Map<String, Object> add(@RequestParam Long EventId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        visitedService.addVisited(email, EventId);
        return Map.of("ok", true);
    }

    // ✅ 다녀온 전시 삭제: DELETE /api/visited?EventId=388
    @DeleteMapping
    public Map<String, Object> remove(@RequestParam Long EventId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        visitedService.removeVisited(email, EventId);
        return Map.of("ok", true);
    }

    // ✅ 다녀온 전시 목록: GET /api/visited
    @GetMapping
    public List<VisitedItemResponse> list(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return visitedService.listVisited(email);
    }

    // ✅ 다녀온 전시 개수: GET /api/visited/count
    @GetMapping("/count")
    public Map<String, Object> count(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return Map.of("count", visitedService.countVisited(email));
    }
}
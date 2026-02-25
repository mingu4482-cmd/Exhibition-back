package com.example.muse.controller;

import com.example.muse.dto.MyReviewItemResponse;
import com.example.muse.dto.ReviewCreateRequest;
import com.example.muse.dto.ReviewTodoItemResponse;
import com.example.muse.dto.ReviewUpdateRequest;
import com.example.muse.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // A) 작성 가능 목록: GET /api/reviews/todo
    @GetMapping("/todo")
    public List<ReviewTodoItemResponse> todo(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return reviewService.todo(email);
    }

    // B) 내가 쓴 후기 목록: GET /api/reviews/mine
    @GetMapping("/mine")
    public List<MyReviewItemResponse> mine(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return reviewService.mine(email);
    }

    // 후기 작성: POST /api/reviews  body: { visitedId, rating, content }
    @PostMapping
    public Map<String, Object> create(@RequestBody @Valid ReviewCreateRequest req,
                                      Authentication auth) {
        String email = (String) auth.getPrincipal();
        reviewService.create(email, req);
        return Map.of("ok", true);
    }

    // 후기 수정: PATCH /api/reviews/{reviewId}
    @PatchMapping("/{reviewId}")
    public Map<String, Object> update(@PathVariable Long reviewId,
                                      @RequestBody @Valid ReviewUpdateRequest req,
                                      Authentication auth) {
        String email = (String) auth.getPrincipal();
        reviewService.update(email, reviewId, req);
        return Map.of("ok", true);
    }

    // 후기 삭제: DELETE /api/reviews/{reviewId}
    @DeleteMapping("/{reviewId}")
    public Map<String, Object> delete(@PathVariable Long reviewId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        reviewService.delete(email, reviewId);
        return Map.of("ok", true);
    }

    // 작성 후기 카운트: GET /api/reviews/count
    @GetMapping("/count")
    public Map<String, Object> count(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return Map.of("count", reviewService.count(email));
    }
}
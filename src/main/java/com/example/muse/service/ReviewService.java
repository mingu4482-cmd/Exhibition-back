package com.example.muse.service;

import com.example.muse.domain.Review;
import com.example.muse.domain.User;
import com.example.muse.domain.Visited;
import com.example.muse.dto.MyReviewItemResponse;
import com.example.muse.dto.ReviewCreateRequest;
import com.example.muse.dto.ReviewTodoItemResponse;
import com.example.muse.dto.ReviewUpdateRequest;
import com.example.muse.repository.ReviewRepository;
import com.example.muse.repository.UserRepository;
import com.example.muse.repository.VisitedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserRepository userRepository;
    private final VisitedRepository visitedRepository;
    private final ReviewRepository reviewRepository;

    private Long userIdByEmail(String email) {
        User u = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return u.getId();
    }

    // A) 작성 가능 목록
    @Transactional(readOnly = true)
    public List<ReviewTodoItemResponse> todo(String email) {
        Long userId = userIdByEmail(email);
        return reviewRepository.findTodos(userId);
    }

    // B) 내가 쓴 후기 목록
    @Transactional(readOnly = true)
    public List<MyReviewItemResponse> mine(String email) {
        Long userId = userIdByEmail(email);
        return reviewRepository.findMine(userId);
    }

    // 후기 작성 (visitedId 기반)
    @Transactional
    public void create(String email, ReviewCreateRequest req) {
        Long userId = userIdByEmail(email);

        Visited v = visitedRepository.findById(req.getVisitedId())
                .orElseThrow(() -> new IllegalArgumentException("관람내역(visited)을 찾을 수 없습니다."));

        if (!v.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 관람내역만 후기 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByVisitedId(v.getId())) {
            throw new IllegalArgumentException("이미 후기가 작성된 관람내역입니다.");
        }

        Integer rating = req.getRating();
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("별점은 1~5만 가능합니다.");
        }

        Review r = new Review();
        r.setVisitedId(v.getId());
        r.setUserId(userId);
        r.setPerformanceId(v.getPerformanceId());
        r.setRating(req.getRating());
        r.setContent(req.getContent() == null ? null : req.getContent().trim());

        reviewRepository.save(r);
    }

    // 후기 수정 (reviewId 기반)
    @Transactional
    public void update(String email, Long reviewId, ReviewUpdateRequest req) {
        Long userId = userIdByEmail(email);

        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));

        if (!r.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 후기만 수정할 수 있습니다.");
        }

        Integer rating = req.getRating();
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("별점은 1~5만 가능합니다.");
        }

        if (req.getRating() != null) r.setRating(req.getRating());
        if (req.getContent() != null) r.setContent(req.getContent().trim());
        // updatedAt은 @UpdateTimestamp가 자동 처리
    }

    // 후기 삭제
    @Transactional
    public void delete(String email, Long reviewId) {
        Long userId = userIdByEmail(email);

        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다."));

        if (!r.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인 후기만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(r);
    }

    // 마이페이지 작성 후기 카운트
    @Transactional(readOnly = true)
    public long count(String email) {
        Long userId = userIdByEmail(email);
        return reviewRepository.countByUserId(userId);
    }
}
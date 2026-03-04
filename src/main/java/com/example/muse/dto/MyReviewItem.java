package com.example.muse.dto;

import java.time.LocalDateTime;

public record MyReviewItem(
        Long reviewId,
        Long visitedId,
        Long EventId,
        String title,
        String posterUrl,
        Integer rating,
        String contentPreview,
        LocalDateTime createdAt
) {}
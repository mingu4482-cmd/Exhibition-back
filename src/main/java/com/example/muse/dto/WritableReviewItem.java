package com.example.muse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WritableReviewItem(
        Long visitedId,
        Long performanceId,
        String title,
        String posterUrl,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime visitedAt
) {}
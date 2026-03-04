package com.example.muse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MyReviewItemResponse {
    Long getReviewId();
    Long getVisitedId();
    Long getEventId();
    String getTitle();
    String getPosterUrl();
    LocalDate getStartDate();
    LocalDate getEndDate();

    Integer getRating();
    String getContent();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getVisitedAt();
}
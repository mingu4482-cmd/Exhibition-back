package com.example.muse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReviewTodoItemResponse {
    Long getVisitedId();
    Long getPerformanceId();
    String getTitle();
    String getPosterUrl();
    LocalDate getStartDate();
    LocalDate getEndDate();
    LocalDateTime getVisitedAt();
}
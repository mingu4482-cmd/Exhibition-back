package com.example.muse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface VisitedItemResponse {
    Long getEventId();
    String getTitle();
    String getPosterUrl();
    LocalDate getStartDate();
    LocalDate getEndDate();
    LocalDateTime getVisitedAt();
}
package com.example.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FavoriteItemResponse {
    private Long performanceId;
    private String title;
    private String posterUrl;
    private LocalDate startDate;
    private LocalDate endDate;
}
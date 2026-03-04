package com.example.muse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FavoriteItemResponse {

    private Long eventId;        // ✅ eventId (기존 getEventId X)
    private String title;
    private String imageUrl;     // ✅ event 테이블: image_url
    private String placeName;    // ✅ event 테이블: place_name
    private LocalDate startDate;
    private LocalDate endDate;
}
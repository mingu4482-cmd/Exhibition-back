package com.example.muse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewCreateRequest {

    @NotNull
    private Long visitedId;

    @Min(1) @Max(5)
    private Integer rating; // 선택이면 null 허용

    @Size(max = 2000)
    private String content; // 선택이면 null 허용

    public ReviewCreateRequest() {}

    public Long getVisitedId() { return visitedId; }
    public void setVisitedId(Long visitedId) { this.visitedId = visitedId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
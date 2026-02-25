package com.example.muse.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ReviewUpdateRequest {

    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 2000)
    private String content;

    public ReviewUpdateRequest() {}

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
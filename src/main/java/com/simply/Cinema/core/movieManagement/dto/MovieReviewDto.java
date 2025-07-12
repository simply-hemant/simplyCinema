package com.simply.Cinema.core.movieManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovieReviewDto {

    private Long id;

    private Long movieId;

    private Long userId;

    private Double rating;

    private String reviewText;

    private Boolean isApproved;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

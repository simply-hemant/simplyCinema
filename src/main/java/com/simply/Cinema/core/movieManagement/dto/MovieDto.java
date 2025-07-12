package com.simply.Cinema.core.movieManagement.dto;

import jakarta.validation.constraints.*;
import com.simply.Cinema.core.movieManagement.eunm.MovieRating;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MovieDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 5000, message = "Description is too long")
    private String description;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @NotNull(message = "Rating is required")
    private MovieRating rating;

    private String trailerUrl;

    private String posterUrl;

    private String bannerUrl;

    private Boolean isActive = true;

    private List<Long> genreIds;

    private List<Long> languageIds;

    private List<String> cast;

    private List<String> crew;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

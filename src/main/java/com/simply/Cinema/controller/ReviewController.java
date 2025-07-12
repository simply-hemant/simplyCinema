package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.dto.MovieReviewDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.movieManagement.impl.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/movies/{movieId}")
    public ResponseEntity<MovieReviewDto> submitReview(@PathVariable Long movieId,
                                                       @RequestBody MovieReviewDto reviewDto)
            throws ValidationException, ResourceNotFoundException {
        reviewDto.setMovieId(movieId);
        return ResponseEntity.ok(reviewService.submitReview(reviewDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieReviewDto> updateReview(@PathVariable Long id,
                                                       @RequestBody MovieReviewDto reviewDto)
            throws ResourceNotFoundException, AuthorizationException {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDto));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id)
            throws ResourceNotFoundException, AuthorizationException {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<List<MovieReviewDto>> getReviews(@PathVariable Long movieId)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(reviewService.getReviewsByMovie(movieId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/moderate")
    public ResponseEntity<MovieReviewDto> moderateReview(@PathVariable Long id)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(reviewService.moderateReview(id));
    }

}

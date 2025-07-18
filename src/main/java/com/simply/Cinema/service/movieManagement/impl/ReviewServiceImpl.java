package com.simply.Cinema.service.movieManagement.impl;

import com.simply.Cinema.core.movieManagement.dto.MovieReviewDto;
import com.simply.Cinema.core.movieManagement.entity.Movie;
import com.simply.Cinema.core.movieManagement.entity.MovieReview;
import com.simply.Cinema.core.movieManagement.repository.MovieRepo;
import com.simply.Cinema.core.movieManagement.repository.MovieReviewRepo;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.movieManagement.ReviewService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final MovieReviewRepo movieReviewRepo;
    private final MovieRepo movieRepo;
    private final UserRepo userRepo;


    @Override
    public MovieReviewDto submitReview(MovieReviewDto reviewDto) throws ValidationException, ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

         // Validate Rating
        if (reviewDto.getRating() == null || reviewDto.getRating() < 0 || reviewDto.getRating() > 10) {
            throw new ValidationException("Rating must be between 0 and 10.");
        }

        Movie movie  = movieRepo.findById(reviewDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: "+ reviewDto.getMovieId()));

        //Prevent duplicate review
        if (movieReviewRepo.existsByUserIdAndMovieId(currentUserId, reviewDto.getMovieId())) {
            throw new BusinessException("You have already submitted a review for this movie.");
        }

        //Create and Save review
        MovieReview review = new MovieReview();
        review.setMovie(movie);
        review.setUser(userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found.")));
        review.setRating(reviewDto.getRating());
        review.setReviewText(reviewDto.getReviewText());
        review.setApproved(false); // Default to false until admin/mod approves
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        MovieReview savedReview = movieReviewRepo.save(review);

        MovieReviewDto response = new MovieReviewDto();
        response.setId(savedReview.getId());
        response.setMovieId(savedReview.getMovie().getId());
        response.setUserId(savedReview.getUser().getId());
        response.setRating(savedReview.getRating());
        response.setReviewText(savedReview.getReviewText());
        response.setIsApproved(savedReview.isApproved());
        response.setCreatedAt(savedReview.getCreatedAt());

        return response;
    }

    @Override
    public MovieReviewDto updateReview(Long reviewId, MovieReviewDto reviewDto) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        MovieReview review = movieReviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        // Only the user who submitted the review can update it
        if (!review.getUser().getId().equals(currentUserId)) {
            throw new AuthorizationException("You are not allowed to update this review.");
        }

        // Update review fields
        if (reviewDto.getRating() != null) {
            review.setRating(reviewDto.getRating());
        }

        if (reviewDto.getReviewText() != null) {
            review.setReviewText(reviewDto.getReviewText());
        }

        review.setUpdatedAt(LocalDateTime.now());
        review.setApproved(false); // Need re-approval after update

        // Save updated review
        MovieReview updatedReview = movieReviewRepo.save(review);

        // Map to DTO
        MovieReviewDto response = new MovieReviewDto();
        response.setId(updatedReview.getId());
        response.setMovieId(updatedReview.getMovie().getId());
        response.setUserId(updatedReview.getUser().getId());
        response.setRating(updatedReview.getRating());
        response.setReviewText(updatedReview.getReviewText());
        response.setIsApproved(updatedReview.isApproved());
        response.setCreatedAt(updatedReview.getCreatedAt());
        response.setUpdatedAt(updatedReview.getUpdatedAt());

        return response;
    }

    @Override
    public void deleteReview(Long reviewId) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        MovieReview review = movieReviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        boolean isOwner = review.getUser().getId().equals(currentUserId);
        boolean isAdmin = SecurityUtil.hasRole("ROLE_ADMIN");

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not authorized to delete this review.");
        }

        movieReviewRepo.delete(review);

    }

    @Override
    public List<MovieReviewDto> getReviewsByMovie(Long movieId) throws ResourceNotFoundException {

        // Check if movie exists
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        // Get only approved reviews
        List<MovieReview> reviews = movieReviewRepo.findByMovieIdAndIsApprovedTrue(movieId);

        // Convert to DTOs manually (without stream)
        List<MovieReviewDto> dtos = new ArrayList<>();

        for (MovieReview review : reviews) {
            MovieReviewDto dto = new MovieReviewDto();
            dto.setId(review.getId());
            dto.setMovieId(movieId);
            dto.setUserId(review.getUser().getId());
            dto.setRating(review.getRating());
            dto.setReviewText(review.getReviewText());
            dto.setIsApproved(review.isApproved());
            dto.setCreatedAt(review.getCreatedAt());
            dto.setUpdatedAt(review.getUpdatedAt());

            dtos.add(dto);
        }

        return dtos;
    }



    @Override
    public Double calculateAverageRating(Long movieId) throws ResourceNotFoundException {

        // Check if movie exists
        if (!movieRepo.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie not found with id: " + movieId);
        }

        // Get average rating of approved reviews
        Double avgRating = movieReviewRepo.getAverageRating(movieId);

        if (avgRating == null) {          // Return 0.0 if no reviews
            return 0.0;
        }

        return avgRating;
    }


    @Override
    public MovieReviewDto moderateReview(Long reviewId) throws ResourceNotFoundException {

        MovieReview review = movieReviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setApproved(true);
        review.setUpdatedAt(LocalDateTime.now());

        MovieReview updated = movieReviewRepo.save(review);

        MovieReviewDto dto = new MovieReviewDto();
        dto.setId(updated.getId());
        dto.setMovieId(updated.getMovie().getId());
        dto.setUserId(updated.getUser().getId());
        dto.setRating(updated.getRating());
        dto.setReviewText(updated.getReviewText());
        dto.setIsApproved(updated.isApproved());
        dto.setCreatedAt(updated.getCreatedAt());
        dto.setUpdatedAt(updated.getUpdatedAt());

        return dto;
    }

}

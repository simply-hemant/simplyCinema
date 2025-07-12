package com.simply.Cinema.service.movieManagement.impl;

import com.simply.Cinema.core.movieManagement.dto.MovieReviewDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;

import java.util.List;

public interface ReviewService {

    MovieReviewDto submitReview(MovieReviewDto reviewDto)
            throws ValidationException, ResourceNotFoundException;

    MovieReviewDto updateReview(Long reviewId, MovieReviewDto reviewDto)
            throws ResourceNotFoundException, AuthorizationException;

    void deleteReview(Long reviewId)
            throws ResourceNotFoundException, AuthorizationException;

    List<MovieReviewDto> getReviewsByMovie(Long movieId)
            throws ResourceNotFoundException;

    Double calculateAverageRating(Long movieId)
            throws ResourceNotFoundException;

    MovieReviewDto moderateReview(Long reviewId)
            throws ResourceNotFoundException;
}

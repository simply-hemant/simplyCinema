package com.simply.Cinema.core.movieManagement.repository;

import com.simply.Cinema.core.movieManagement.entity.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieReviewRepo extends JpaRepository<MovieReview, Long> {

    List<MovieReview> findByMovieId(Long movieId);

    List<MovieReview> findByUserId(Long userId);

    List<MovieReview> findByMovieIdAndIsApprovedTrue(Long movieId);

    List<MovieReview> findByIsApprovedFalse();

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    @Query("SELECT AVG(mr.rating) FROM MovieReview mr WHERE mr.movie.id = :movieId AND mr.isApproved = true")
    Double getAverageRating(Long movieId);

    @Query("SELECT COUNT(mr) FROM MovieReview mr WHERE mr.movie.id = :movieId AND mr.isApproved = true")
    Long getReviewCount(Long movieId);

}

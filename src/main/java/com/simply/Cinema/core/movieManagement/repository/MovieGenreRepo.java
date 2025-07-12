package com.simply.Cinema.core.movieManagement.repository;

import com.simply.Cinema.core.movieManagement.entity.Genre;
import com.simply.Cinema.core.movieManagement.entity.Movie;
import com.simply.Cinema.core.movieManagement.entity.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieGenreRepo extends JpaRepository<MovieGenre, Long> {

    @Query("SELECT mg.genre FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    List<Genre> findGenresByMovieId(Long movieId);

    @Query("SELECT mg.movie FROM MovieGenre mg WHERE mg.genre.id = :genreId")
    List<Movie> findMoviesByGenreId(Long genreId);

    void deleteByMovieIdAndGenreId(Long movieId, Long genreId);

    void deleteByMovieId(Long movieId);

    //Check if a specific movie-genre mapping exists
    Optional<MovieGenre> findByMovieIdAndGenreId(Long movieId, Long genreId);


}

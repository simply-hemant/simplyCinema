package com.simply.Cinema.core.movieManagement.repository;

import com.simply.Cinema.core.movieManagement.entity.Language;
import com.simply.Cinema.core.movieManagement.entity.Movie;
import com.simply.Cinema.core.movieManagement.entity.MovieGenre;
import com.simply.Cinema.core.movieManagement.entity.MovieLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieLanguageRepo extends JpaRepository<MovieGenre, Long> {

//    @Query("SELECT ml.language FROM MovieLanguage ml WHERE ml.movie.id = :movieId")
//    List<Language> findLanguagesByMovieId(Long movieId);
//
//    @Query("SELECT ml.movie FROM MovieLanguage ml WHERE ml.language.id = :languageId")
//    List<Movie> findMoviesByLanguageId(Long languageId);
//
//    Optional<MovieLanguage> findByMovieIdAndLanguageId(Long movieId, Long languageId);
//
//    void deleteByMovieIdAndLanguageId(Long movieId, Long languageId);
//
//    void deleteByMovieId(Long movieId);

}

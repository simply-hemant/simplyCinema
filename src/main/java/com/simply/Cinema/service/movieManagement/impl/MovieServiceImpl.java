package com.simply.Cinema.service.movieManagement.impl;

import com.simply.Cinema.core.movieManagement.dto.MovieDto;
import com.simply.Cinema.core.movieManagement.entity.*;
import com.simply.Cinema.core.movieManagement.eunm.MovieRating;
import com.simply.Cinema.core.movieManagement.repository.*;
import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.movieManagement.MovieService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepo movieRepo;
    private final AuditLogService auditLogService;
    private final GenreRepo genreRepo;
    private final LanguageRepo languageRepo;
    private final MovieGenreRepo movieGenreRepo;
    private final MovieLanguageRepo movieLanguageRepo;

    @Override
    public MovieDto createMovie(MovieDto movieDto) throws ValidationException, BusinessException {
        long currentUserId = SecurityUtil.getCurrentUserId();

        if (movieDto.getTitle() == null || movieDto.getTitle().isBlank()) {
            throw new ValidationException("Movie title is required.");
        }

        if (movieRepo.findByTitle(movieDto.getTitle()) != null) {
            throw new BusinessException("Movie with title '" + movieDto.getTitle() + "' already exists.");
        }

        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setDurationMinutes(movieDto.getDurationMinutes());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setRating(movieDto.getRating());
        movie.setTrailerUrl(movieDto.getTrailerUrl());
        movie.setPosterUrl(movieDto.getPosterUrl());
        movie.setBannerUrl(movieDto.getBannerUrl());
        movie.setCast(movieDto.getCast());
        movie.setCrew(movieDto.getCrew());
        movie.setCreatedBy(currentUserId);
        movie.setCreatedAt(LocalDateTime.now());
        movie.setGenres(new ArrayList<>());
        movie.setLanguages(new ArrayList<>());

        Movie savedMovie = movieRepo.save(movie);

        // Save Genre Mappings
        if (movieDto.getGenreIds() != null) {
            for (Long genreId : movieDto.getGenreIds()) {
                Genre genre = genreRepo.findById(genreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found: " + genreId));

                MovieGenre mg = new MovieGenre();
                mg.setMovie(savedMovie);
                mg.setGenre(genre);
                savedMovie.getGenres().add(mg);
            }
        }

        // Save Language Mappings
        if (movieDto.getLanguageIds() != null) {
            for (Long languageId : movieDto.getLanguageIds()) {
                Language language = languageRepo.findById(languageId)
                        .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + languageId));

                MovieLanguage ml = new MovieLanguage();
                ml.setMovie(savedMovie);
                ml.setLanguage(language);
                savedMovie.getLanguages().add(ml);
            }
        }

        movieRepo.save(savedMovie);

        auditLogService.logEvent("movies", AuditAction.CREATE, savedMovie.getId(), currentUserId);

        return mapToDto(savedMovie);
    }

    @Override
    public MovieDto updateMovie(Long movieId, MovieDto movieDto) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id."));

        if(!movie.getCreatedBy().equals(currentUserId)) {
            throw new AuthorizationException("Access denied. You did not added this movie.");
        }

        // Basic field updates
        if (movieDto.getTitle() != null) movie.setTitle(movieDto.getTitle());
        if (movieDto.getDescription() != null) movie.setDescription(movieDto.getDescription());
        if (movieDto.getDurationMinutes() != 0) movie.setDurationMinutes(movieDto.getDurationMinutes());
        if (movieDto.getReleaseDate() != null) movie.setReleaseDate(movieDto.getReleaseDate());
        if (movieDto.getRating() != null) movie.setRating(movieDto.getRating());
        if (movieDto.getTrailerUrl() != null) movie.setTrailerUrl(movieDto.getTrailerUrl());
        if (movieDto.getPosterUrl() != null) movie.setPosterUrl(movieDto.getPosterUrl());
        if (movieDto.getBannerUrl() != null) movie.setBannerUrl(movieDto.getBannerUrl());
        if (movieDto.getCast() != null) movie.setCast(movieDto.getCast());
        if (movieDto.getCrew() != null) movie.setCrew(movieDto.getCrew());


        // === GENRE UPDATE ===
        if (movieDto.getGenreIds() != null) {
            movie.getGenres().clear();  // Clear old associations

            for (Long genreId : movieDto.getGenreIds()) {
                Genre genre = genreRepo.findById(genreId)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre not found: " + genreId));

                MovieGenre movieGenre = new MovieGenre();
                movieGenre.setMovie(movie);
                movieGenre.setGenre(genre);
                movie.getGenres().add(movieGenre);
            }
        }

        // === LANGUAGE UPDATE ===
        if (movieDto.getLanguageIds() != null) {
            movie.getLanguages().clear();

            for (Long langId : movieDto.getLanguageIds()) {
                Language language = languageRepo.findById(langId)
                        .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + langId));

                MovieLanguage movieLanguage = new MovieLanguage();
                movieLanguage.setMovie(movie);
                movieLanguage.setLanguage(language);
                movie.getLanguages().add(movieLanguage);
            }
        }

        // Map saved movie to DTO
        Movie updatedMovie = movieRepo.save(movie);
        auditLogService.logEvent("movies", AuditAction.UPDATE, updatedMovie.getId(), currentUserId);
        return mapToDto(updatedMovie);
    }

    @Override
    public void deleteMovie(Long movieId) throws ResourceNotFoundException, BusinessException {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id."));

        if(!movie.getCreatedBy().equals(currentUserId)) {
            throw new AuthorizationException("Access denied. You did not added this movie.");
        }

        movieRepo.deleteById(movieId);

    }

    @Override
    public MovieDto getMovieById(Long movieId) throws ResourceNotFoundException {

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id."));

       return mapToDto(movie);
    }

//    public Page<MovieDto> searchMovies(String keyword, int page, int size) throws ValidationException {
//        if (keyword == null || keyword.isBlank()) {
//            throw new ValidationException("Search keyword must not be empty.");
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Movie> moviePage = movieRepo.searchMovies(keyword, pageable);
//
//        return moviePage.map(movie -> {
//            MovieDto dto = new MovieDto();
//            dto.setId(movie.getId());
//            dto.setTitle(movie.getTitle());
//            dto.setDescription(movie.getDescription());
//            dto.setDurationMinutes(movie.getDurationMinutes());
//            dto.setReleaseDate(movie.getReleaseDate());
//            dto.setRating(movie.getRating());
//            dto.setTrailerUrl(movie.getTrailerUrl());
//            dto.setPosterUrl(movie.getPosterUrl());
//            dto.setBannerUrl(movie.getBannerUrl());
//            dto.setIsActive(movie.isActive());
//            dto.setCast(movie.getCast());
//            dto.setCrew(movie.getCrew());
//
//            List<Long> genreIds = new ArrayList<>();
//            for (MovieGenre g : movie.getGenres()) {
//                genreIds.add(g.getGenre().getId());
//            }
//            dto.setGenreIds(genreIds);
//
//            List<Long> languageIds = new ArrayList<>();
//            for (MovieLanguage l : movie.getLanguages()) {
//                languageIds.add(l.getLanguage().getId());
//            }
//            dto.setLanguageIds(languageIds);
//
//            return dto;
//        });
//    }

    @Override
    public Page<MovieDto> searchMovies(String keyword, int page, int size) throws ValidationException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ValidationException("Keyword required");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepo.searchMovies(keyword, pageable);

        List<MovieDto> dtoList = new ArrayList<>();
        for (Movie m : moviePage.getContent()) {
            dtoList.add(mapToDto(m));
        }

        return new PageImpl<MovieDto>(dtoList, pageable, moviePage.getTotalElements());
    }

    @Override
    public Page<MovieDto> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepo.findAll(pageable);

        List<MovieDto> dtoList = new ArrayList<MovieDto>();
        for (Movie m : moviePage.getContent()) {
            dtoList.add(mapToDto(m));
        }

        return new PageImpl<MovieDto>(dtoList, pageable, moviePage.getTotalElements());
    }


//    @Override
//    public Page<MovieDto> getAllMovies(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Movie> moviePage = movieRepo.findAll(pageable);
//
//        return moviePage.map(movie -> {
//            MovieDto dto = new MovieDto();
//
//            dto.setId(movie.getId());
//            dto.setTitle(movie.getTitle());
//            dto.setDescription(movie.getDescription());
//            dto.setDurationMinutes(movie.getDurationMinutes());
//            dto.setReleaseDate(movie.getReleaseDate());
//            dto.setRating(movie.getRating());
//            dto.setTrailerUrl(movie.getTrailerUrl());
//            dto.setPosterUrl(movie.getPosterUrl());
//            dto.setBannerUrl(movie.getBannerUrl());
//            dto.setIsActive(movie.isActive());
//            dto.setCast(movie.getCast());
//            dto.setCrew(movie.getCrew());
//
//            List<Long> genreIds = new ArrayList<>();
//            for (MovieGenre g : movie.getGenres()) {
//                genreIds.add(g.getGenre().getId());
//            }
//            dto.setGenreIds(genreIds);
//
//            List<Long> languageIds = new ArrayList<>();
//            for (MovieLanguage l : movie.getLanguages()) {
//                languageIds.add(l.getLanguage().getId());
//            }
//            dto.setLanguageIds(languageIds);
//
//            return dto;
//        });
//    }

    private MovieDto mapToDto(Movie movie) {
        MovieDto dto = new MovieDto();

        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setRating(movie.getRating());
        dto.setTrailerUrl(movie.getTrailerUrl());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setBannerUrl(movie.getBannerUrl());
        dto.setIsActive(movie.isActive());
        dto.setCast(movie.getCast());
        dto.setCrew(movie.getCrew());
        dto.setCreatedAt(movie.getCreatedAt());
        dto.setUpdatedAt(movie.getUpdatedAt());

        // Genre IDs
        List<Long> genreIds = new ArrayList<>();
        if (movie.getGenres() != null) {
            for (MovieGenre mg : movie.getGenres()) {
                genreIds.add(mg.getGenre().getId());
            }
        }
        dto.setGenreIds(genreIds);

        // Language IDs
        List<Long> languageIds = new ArrayList<>();
        if (movie.getLanguages() != null) {
            for (MovieLanguage ml : movie.getLanguages()) {
                languageIds.add(ml.getLanguage().getId());
            }
        }
        dto.setLanguageIds(languageIds);

        return dto;
    }




}

package com.simply.Cinema.controller;

import com.simply.Cinema.core.movieManagement.dto.MovieDto;
import com.simply.Cinema.core.movieManagement.repository.MovieRepo;
import com.simply.Cinema.service.movieManagement.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.management.MonitorInfo;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieRepo movieRepo;
    private final MovieService movieService;

//    @GetMapping("/all")
//    public ResponseEntity<List<MovieDto>> getAllMovies (){
//
//        List<MovieDto> movieDtoList = movieService.getAllMovies();
//        return ResponseEntity.ok(movieDtoList);
//    }

    @PreAuthorize("hasRole('THEATRE_OWNER', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<MovieDto> createMovie(@RequestBody MovieDto movieDto){

        MovieDto created = movieService.createMovie(movieDto);
        return ResponseEntity.ok(created);

    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovie(
            @RequestBody MovieDto movieDto,
            @PathVariable Long movieId){

        MovieDto movieDto1 = movieService.updateMovie(movieId, movieDto);
        return ResponseEntity.ok(movieDto1);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PostMapping("/delete/{movieId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId){

        movieService.deleteMovie(movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long movieId){

        MovieDto movieDto = movieService.getMovieById(movieId);
        return ResponseEntity.ok(movieDto);

    }

    @GetMapping("/all")
    public ResponseEntity<Page<MovieDto>> getAllMovies(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize){

        Page<MovieDto> movieDtoPage = movieService.getAllMovies(pageNo, pageSize);
        return ResponseEntity.ok(movieDtoPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieDto>> searchMovie(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize){

        Page<MovieDto> search = movieService.searchMovies(keyword, pageNo, pageSize);
        return ResponseEntity.ok(search);

    }


}

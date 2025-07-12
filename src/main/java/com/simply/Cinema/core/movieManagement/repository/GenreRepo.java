package com.simply.Cinema.core.movieManagement.repository;

import com.simply.Cinema.core.movieManagement.entity.Genre;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Registered
public interface GenreRepo extends JpaRepository<Genre, Long> {

    Genre findByName(String name);

    List<Genre> findByMoviesIsNotEmpty(); // Only genres linked to movies (active classification)

    boolean existsByNameIgnoreCase(String name);


}

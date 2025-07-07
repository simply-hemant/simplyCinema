package com.simply.Cinema.core.location_and_venue.repository;

import com.simply.Cinema.core.location_and_venue.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepo extends JpaRepository<Screen , Long> {

    boolean existsByNameIgnoreCaseAndTheatreId(String name, Long theatreId);

    List<Screen> findScreenByTheatreId(Long theatreId);


}
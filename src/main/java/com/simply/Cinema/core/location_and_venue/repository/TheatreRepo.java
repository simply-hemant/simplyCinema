package com.simply.Cinema.core.location_and_venue.repository;

import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepo extends JpaRepository<Theatre , Long> {
}

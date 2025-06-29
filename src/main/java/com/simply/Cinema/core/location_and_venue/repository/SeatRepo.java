package com.simply.Cinema.core.location_and_venue.repository;

import com.simply.Cinema.core.location_and_venue.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepo extends JpaRepository<Seat , Long> {
}

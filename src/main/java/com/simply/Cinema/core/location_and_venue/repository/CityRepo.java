package com.simply.Cinema.core.location_and_venue.repository;

import com.simply.Cinema.core.location_and_venue.dto.CityDto;
import com.simply.Cinema.core.location_and_venue.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepo extends JpaRepository<City, Long> {

    boolean existsByNameIgnoreCaseAndStateIgnoreCase(String name, String state);

    // 🔹 Get all active cities
    List<City> findByIsActiveTrue();

    // 🔹 Find cities by state
    List<City> findByStateIgnoreCase(String state);

    // 🔹 Find cities by country
    List<City> findByCountryIgnoreCase(String country);

    // 🔹 Search cities by name, state, or country (contains keyword)
    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.state) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.country) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<City> searchCities(@Param("keyword") String keyword);

    //above is JPQL(Java Persistence Query Language), not raw SQL.
}

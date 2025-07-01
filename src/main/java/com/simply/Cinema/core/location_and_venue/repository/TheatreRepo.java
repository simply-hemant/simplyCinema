package com.simply.Cinema.core.location_and_venue.repository;

import org.springframework.data.domain.Page;       // ✅ Correct
import org.springframework.data.domain.Pageable;   // ✅ Correct
import org.springframework.data.domain.PageRequest;
import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TheatreRepo extends JpaRepository<Theatre , Long> {

    boolean existsByNameIgnoreCaseAndCityId(String name, Long cityId);

    List<Theatre> findByCityId(Long cityId);

    List<Theatre> findTheatreByOwnerId(Long ownerId);

    @Query(value = """
    SELECT t.* FROM theatre t
    JOIN theatre_amenities ta ON t.id = ta.theatre_id
    WHERE ta.amenity IN (:amenities)
    GROUP BY t.id
    HAVING COUNT(DISTINCT ta.amenity) = :amenityCount
""", nativeQuery = true)
    List<Theatre> findByAllAmenities(
            @Param("amenities") List<String> amenities,
            @Param("amenityCount") long amenityCount
    );


    @Query("SELECT t FROM Theatre t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Theatre> findByNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

}

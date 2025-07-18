package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.show_and_booking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepo extends JpaRepository<Show, Long> {

    Optional<Show> findById(Long id);

    List<Show> findByMovie_Id(Long movieId);

    List<Show> findByScreen_IdIn(List<Long> screenIds);

    List<Show> findByShowDate(LocalDate date);

    @Query("SELECT s FROM Show s WHERE s.showDate BETWEEN :startDate AND :endDate")
    List<Show> findByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Show s WHERE s.status = 'UPCOMING'")
    List<Show> findUpcomingShows();

    @Query("SELECT s FROM Show s WHERE s.status = 'ACTIVE'")
    List<Show> findActiveShows();

    @Query("SELECT s FROM Show s WHERE s.screen.theatre.id = :theatreId")
    List<Show> findShowsByTheatre(@Param("theatreId") Long theatreId);

    @Query("SELECT s FROM Show s JOIN ShowSeat ss ON s.id = ss.show.id " +
            "WHERE s.movie.id = :movieId AND s.showDate = :date AND ss.status = 'AVAILABLE'")
    List<Show> findShowsWithAvailableSeats(@Param("movieId") Long movieId,
                                           @Param("date") LocalDate date);


    boolean existsByScreen_IdAndShowDateAndShowTime(Long screenId, LocalDate showDate, LocalTime showTime);
}

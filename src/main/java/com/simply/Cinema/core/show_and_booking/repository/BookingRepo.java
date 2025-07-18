package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.show_and_booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    Optional<Booking> findById(Long id);

    List<Booking> findByUser_Id(Long userId);

    Optional<Booking> findByBookingReference(String reference);

    List<Booking> findByShow_Id(Long showId);

    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Booking> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.bookingStatus = 'CONFIRMED' AND b.show.showDate >= CURRENT_DATE")
    List<Booking> findUpcomingBookings(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.show.showDate < CURRENT_DATE")
    List<Booking> findBookingHistory(@Param("userId") Long userId);

}

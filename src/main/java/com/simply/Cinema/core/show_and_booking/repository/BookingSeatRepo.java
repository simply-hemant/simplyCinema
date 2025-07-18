package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.show_and_booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepo extends JpaRepository<BookingSeat, Long> {


    List<BookingSeat> findByBooking_Id(Long bookingId);

    List<BookingSeat> findBySeat_Id(Long seatId);

    @Query("SELECT bs.seat FROM BookingSeat bs WHERE bs.booking.id = :bookingId")
    List<Seat> findSeatsByBooking(@Param("bookingId") Long bookingId);


}

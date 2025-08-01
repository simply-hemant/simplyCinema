package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowSeatRepo extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShow_Id(Long showId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ShowSeat ss WHERE ss.show.id = :showId")
    void deleteByShowId(@Param("showId") Long showId);

    List<ShowSeat> findBySeat_Id(Long seatId);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status = 'AVAILABLE'")
    List<ShowSeat> findAvailableSeats(@Param("showId") Long showId);

    @Query("SELECT ss FROM ShowSeat ss WHERE ss.show.id = :showId AND ss.status = 'LOCKED'")
    List<ShowSeat> findLockedSeats(@Param("showId") Long showId);

    @Modifying
    @Query("UPDATE ShowSeat ss SET ss.status = 'LOCKED', ss.lockedByUserId = :userId, ss.lockedUntil = :lockedUntil " +
            "WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds AND ss.status = 'AVAILABLE'")
    int lockSeats(@Param("showId") Long showId,
                  @Param("seatIds") List<Long> seatIds,
                  @Param("userId") Long userId,
                  @Param("lockedUntil") LocalDateTime lockedUntil);

    @Modifying
    @Query("UPDATE ShowSeat ss SET ss.status = 'AVAILABLE', ss.lockedByUserId = NULL, ss.lockedUntil = NULL " +
            "WHERE ss.show.id = :showId AND ss.seat.id IN :seatIds AND ss.status = 'LOCKED'")
    int unlockSeats(@Param("showId") Long showId, @Param("seatIds") List<Long> seatIds);

    @Modifying
    @Query("UPDATE ShowSeat ss SET ss.status = :status WHERE ss.show.id = :showId AND ss.seat.id = :seatId")
    int updateSeatStatus(@Param("showId") Long showId,
                         @Param("seatId") Long seatId,
                         @Param("status") ShowSeatStatus status);

}

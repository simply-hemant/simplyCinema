package com.simply.Cinema.core.show_and_booking.repository;

import com.simply.Cinema.core.show_and_booking.entity.BookingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingPaymentRepo extends JpaRepository<BookingPayment, Long> {

    Optional<BookingPayment> findByBooking_Id(Long bookingId);

    Optional<BookingPayment> findByTransactionId(String transactionId);

    @Query("SELECT p FROM BookingPayment p WHERE p.booking.id = :bookingId AND p.status = 'SUCCESS'")
    List<BookingPayment> findSuccessfulPayments(@Param("bookingId") Long bookingId);

    @Query("SELECT p FROM BookingPayment p WHERE p.booking.id = :bookingId AND p.status = 'FAILED'")
    List<BookingPayment> findFailedPayments(@Param("bookingId") Long bookingId);

}

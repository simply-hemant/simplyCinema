package com.simply.Cinema.core.show_and_booking.entity;

import com.simply.Cinema.core.show_and_booking.Enum.PaymentMethod;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Booking booking;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String transactionId;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String gatewayResponse;

    private LocalDateTime createdAt;
}

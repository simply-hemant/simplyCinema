package com.simply.Cinema.core.show_and_booking.entity;

import com.simply.Cinema.core.show_and_booking.Enum.BookingStatus;
import com.simply.Cinema.core.show_and_booking.Enum.PaymentStatus;
import com.simply.Cinema.core.user.entity.User;
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
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Show show;

    private String bookingReference;

    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private String qrCode; // Base64 or file link

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


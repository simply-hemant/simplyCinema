package com.simply.Cinema.core.show_and_booking.entity;


import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
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
public class ShowSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Show show;

    @ManyToOne
    private Seat seat;

    private Double price;

    @Enumerated(EnumType.STRING)
    private ShowSeatStatus status; // AVAILABLE, LOCKED, BOOKED

    private LocalDateTime lockedUntil;

    private Long lockedByUserId;
}


package com.simply.Cinema.core.location_and_venue.entity;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    private String rowNumber;
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @Column(nullable = false)
    private Boolean isActive = true;

//    // ✅ NEW FIELD: Used for booking status
//    @Column(nullable = false)
//    private Boolean isBooked = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

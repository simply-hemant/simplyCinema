package com.simply.Cinema.core.show_and_booking.entity;

import com.simply.Cinema.core.location_and_venue.entity.Screen;
import com.simply.Cinema.core.movieManagement.entity.Movie;
import com.simply.Cinema.core.show_and_booking.Enum.ShowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Movie movie;

    @NotNull
    @ManyToOne
    private Screen screen;

    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;

    private Double basePrice;
    private Double dynamicPriceMultiplier;

    private Integer totalSeats;
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private ShowStatus status;

    private Long createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


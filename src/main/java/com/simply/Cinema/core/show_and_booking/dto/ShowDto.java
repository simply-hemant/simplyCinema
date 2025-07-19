package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.show_and_booking.Enum.ShowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowDto {

    private Long id;

    private Long movieId;
    private Long screenId;

    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;

    private Double basePrice;
    private Double dynamicPriceMultiplier;

    private Integer totalSeats;
    private Integer availableSeats;

    private ShowStatus status;

    private LocalDateTime createdAt;

    private Map<String, Double> seatPrices;

}


package com.simply.Cinema.core.show_and_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowAvailabilityDto {
    private Long showId;
    private String movieTitle;
    private String screenName;
    private LocalDate showDate;
    private LocalTime showTime;

    private List<SeatAvailabilityDto> availableSeats;
}


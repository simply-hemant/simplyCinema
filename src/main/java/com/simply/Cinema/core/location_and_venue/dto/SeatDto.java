package com.simply.Cinema.core.location_and_venue.dto;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeatDto {

    private Long id;
    private Long screenId;
    private String rowNumber;
    private String seatNumber;
    private SeatType seatType;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

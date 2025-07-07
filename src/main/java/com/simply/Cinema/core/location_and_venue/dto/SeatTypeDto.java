package com.simply.Cinema.core.location_and_venue.dto;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import lombok.Data;

@Data
public class SeatTypeDto {

    private Long id;
    private SeatType seatType;        // Enum: REGULAR, VIP, etc.
    private Double price;             // Price per seat for this type
    private String description;       // Optional description or note
    private Boolean isActive;         // If this type is available
}

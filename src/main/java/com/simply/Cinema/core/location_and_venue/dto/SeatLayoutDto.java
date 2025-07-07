package com.simply.Cinema.core.location_and_venue.dto;

import lombok.Data;

import java.util.List;

@Data
public class SeatLayoutDto {

    private Long screenId;                        // ID of the screen
    private String layoutName;                    // Optional: Name of the layout
    private String createdBy;                     // Optional: Designer or staff who created it

    private List<SeatDto> seats;                  // Actual seat configuration
}

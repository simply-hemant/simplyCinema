package com.simply.Cinema.core.location_and_venue.dto;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import lombok.Data;

import java.util.List;

@Data
public class SeatLayoutDto {

    private Long screenId;                        // ID of the screen
    private String layoutName;                    // Optional: Name of the layout
    private String createdBy;                     // Optional: Designer or staff who created it

    private boolean autoGenerateSeats = true;

    private Integer seatsPerRow; // e.g. 20

    private Integer vipSeatCount;     // e.g. 10
    private Integer premiumSeatCount; // e.g. 150
    private Integer regularSeatCount; // e.g. 100

    private List<SeatDto> seats; // optional in request

}

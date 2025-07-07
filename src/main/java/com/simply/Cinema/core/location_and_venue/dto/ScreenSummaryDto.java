package com.simply.Cinema.core.location_and_venue.dto;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenSummaryDto {
    private Long screenId;
    private String screenName;
    private Integer totalSeats;
    private Integer activeSeats;
    private Map<SeatType, Integer> seatTypeCounts;
}
